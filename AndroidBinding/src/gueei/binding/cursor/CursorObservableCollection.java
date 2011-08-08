package gueei.binding.cursor;
/**
 * User: =ra=
 * Date: 05.08.11
 * Time: 15:23
 */

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Handler;
import gueei.binding.BindingLog;
import gueei.binding.collections.ObservableCollection;

import java.lang.reflect.Field;
import java.util.ArrayList;
// TODO: ? Make spinner disabled if CursorObservableCollection.size()==0
// TODO: ? Automatically reset selectedPosition when setCursor()

/**
 * Recommend to use instead of CursorObservable only after(!) affirmation by Andy Tsuy  (=ra=)
 */
@SuppressWarnings({"UnusedDeclaration"})
public class CursorObservableCollection<T extends CursorRowModel> extends ObservableCollection<T> {

	private final Context                   mContext;
	private       Cursor                    mCursor;
	private final Class<T>                  mRowModelType;
	private final CursorRowModel.Factory<T> mFactory;
	private final ArrayList<Field> mCursorFields = new ArrayList<Field>();
	private int mCursorRowsCount;

	public CursorObservableCollection(Context context, Class<T> rowModelType) {
		//noinspection NullableProblems
		this(context, rowModelType, new DefaultFactory<T>(rowModelType), null);
	}

	public CursorObservableCollection(Context context, Class<T> rowModelType, Cursor cursor) {
		this(context, rowModelType, new DefaultFactory<T>(rowModelType), cursor);
	}

	public CursorObservableCollection(Context context, Class<T> rowModelType, CursorRowModel.Factory<T> factory) {
		//noinspection NullableProblems
		this(context, rowModelType, factory, null);
	}

	public CursorObservableCollection(Context context, Class<T> rowModelType, CursorRowModel.Factory<T> factory,
									  Cursor cursor) {
		mContext = context;
		mRowModelType = rowModelType;
		mFactory = factory;
		mCursor = cursor;
		if (null != mCursor) {
			mCursor.registerDataSetObserver(mCursorDataSetObserver);
		}
		cacheCursorRowCount();
		init();
	}

	public void setCursor(Cursor cursor) {
		if (null != mCursor) {
			unregisterContentObserver();
			mCursor.unregisterDataSetObserver(mCursorDataSetObserver);
		}
		mCursor = cursor;
		if (null != mCursor) {
			mCursor.registerDataSetObserver(mCursorDataSetObserver);
		}
		cacheCursorRowCount();
		this.notifyCollectionChanged();
	}

	public Cursor getCursor() {
		return mCursor;
	}

	/**
	 * There are no obvious methods like AddItem(s), RemoveItem(s), etc for Сursor
	 * data could be changed anywhere and anytime out from model
	 * sometimes we need to know about data changes
	 * Not sure if we have to track more than one uri (!!!)
	 *
	 * @param uri : Uri to track for data changes
	 */
	public void setContentObserverTrackingUri(Uri uri) {
		unregisterContentObserver();
		if (null != uri) {
			registerContentObserver(uri);
		}
	}

	public T getItem(int position) {
		mCursor.moveToPosition(position);
		T row = newRowModel(mContext);
		fillData(row, mCursor);
		return row;
	}

	protected void requery() {
		if (null != mCursor) {
			mCursor.requery();
			// mCursorRowsCount = mCursor.getCount(); // will be handled by mCursorDataSetObserver
		}
	}

	public Class<T> getComponentType() {
		return mRowModelType;
	}

	public int size() {
		// return (null == mCursor) ? 0 : mCursor.getCount(); // too much requests..., cached
		return mCursorRowsCount;
	}

	@Override
	public long getItemId(int position) {
		if (0 < mCursorRowsCount) {
			return getItem(position).getId(position);
		}
		return position;
	}

	public void onLoad(int position) {
	}

	private void cacheCursorRowCount() {
		mCursorRowsCount = (null == mCursor) ? 0 : mCursor.getCount();
	}

	private void init() {
		for (Field f : mRowModelType.getFields()) {
			if (!CursorField.class.isAssignableFrom(f.getType())) {
				continue;
			}
			mCursorFields.add(f);
		}
	}

	private T newRowModel(Context context) {
		T row = mFactory.createRowModel(context);
		row.setCursor(mCursor);
		row.setContext(context);
		return row;
	}

	@SuppressWarnings({"ConstantConditions"})
	private void fillData(T rowModel, Cursor cursor) {
		for (Field f : mCursorFields) {
			try {
				((CursorField<?>) f.get(rowModel)).fillValue(cursor);
			}
			catch (Exception ignored) {
			}
		}
	}

	private static class DefaultFactory<T extends CursorRowModel> implements CursorRowModel.Factory<T> {

		private final Class<T> mRowModelType;

		public DefaultFactory(Class<T> rowModelType) {
			mRowModelType = rowModelType;
		}

		public T createRowModel(Context context) {
			try {
				return mRowModelType.newInstance();
			}
			catch (Exception e) {
				BindingLog.exception("CursorObservable: Factory", e);
				return null;
			}
		}
	}

	private void registerContentObserver(Uri uri) {
		mContext.getContentResolver().registerContentObserver(uri, false, mCursorContentObserver);
	}

	private void unregisterContentObserver() {
		mContext.getContentResolver().unregisterContentObserver(mCursorContentObserver);
	}

	private final ContentObserver mCursorContentObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			requery();
			// notifyCollectionChanged(); // because requery causes DataSetObserver.onChanged() call
		}
	};
	/**
	 * Really don't believe someone cache open cursors in content provider and than
	 * notifies them (cursors) if data changes
	 * but, if cursor is a SQLite cursor directly obtained from a db ...
	 */
	private       DataSetObserver mCursorDataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			cacheCursorRowCount();
			notifyCollectionChanged();
		}
	};

	protected void finalize() throws Throwable {
		try {
			mCursorRowsCount = 0;
			unregisterContentObserver();
			if (null != mCursor) {
				mCursor.unregisterDataSetObserver(mCursorDataSetObserver);
				if (!mCursor.isClosed()) {
					mCursor.close();
				}
				mCursor = null;
			}
		}
		catch (Exception ignored) {
		}
		finally {
			super.finalize();
		}
	}
}

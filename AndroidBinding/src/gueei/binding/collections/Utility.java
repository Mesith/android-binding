package gueei.binding.collections;

import gueei.binding.Binder;
import gueei.binding.IObservableCollection;
import gueei.binding.cursor.CursorObservable;
import gueei.binding.cursor.CursorObservableAdapter;
import gueei.binding.cursor.CursorRowTypeMap;
import gueei.binding.cursor.CursorSourceAdapter;
import android.content.Context;
import android.widget.Adapter;


public class Utility {
	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public static Adapter getSimpleAdapter(
			Context context, Object collection, 
			int layoutId, int dropDownLayoutId) throws Exception{
		if ((collection instanceof IObservableCollection)){
			IObservableCollection obsCollection = (IObservableCollection)collection;
			return new CollectionAdapter(
					Binder.getApplication(), 
					obsCollection, 
					layoutId, 
					dropDownLayoutId);
		}
		if (collection instanceof CursorObservable){
			CursorObservable cobs = (CursorObservable)collection;
			return new CursorObservableAdapter(Binder.getApplication(), 
					cobs, layoutId, dropDownLayoutId);
		}
		if (collection instanceof CursorRowTypeMap){
			CursorRowTypeMap cursor = (CursorRowTypeMap)collection;
			return new CursorSourceAdapter(Binder
					.getApplication(), cursor, layoutId, dropDownLayoutId);
		}
		if (collection.getClass().isArray()){
			return new ArrayAdapter(Binder.getApplication(),
					collection.getClass().getComponentType(),
					(Object[]) collection, layoutId, dropDownLayoutId);
		}
		return null;
	}
}

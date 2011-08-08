package gueei.binding.viewAttributes.adapterView;

import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import gueei.binding.ViewAttribute;
import gueei.binding.collections.LazyLoadAdapter;
import gueei.binding.iConst;

public class AdapterViewAttribute<T extends Adapter> extends ViewAttribute<AdapterView<T>, Adapter> {
	public AdapterViewAttribute(AdapterView<T> view) {
		super(Adapter.class, view, iConst.ATTR_ADAPTER);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doSetAttributeValue(Object newValue) {
		if (newValue instanceof Adapter){
			getView().setAdapter((T)newValue);
			if (newValue instanceof LazyLoadAdapter){
				if (getView() instanceof AbsListView)
				((LazyLoadAdapter)newValue).setRoot((AbsListView)getView());
			}
		}
	}

	@Override
	public Adapter get() {
		return getView().getAdapter();
	}
}
package gueei.binding.widgets.treeview;

import android.widget.ListView;
import gueei.binding.Binder;
import gueei.binding.BindingLog;
import gueei.binding.ConstantObservable;
import gueei.binding.IObservable;
import gueei.binding.IObservableCollection;
import gueei.binding.ISyntaxResolver.SyntaxResolveException;
import gueei.binding.InnerFieldObservable;

public class Utility {
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public static IObservable<?> getObservableByFieldPath(Object dataSource, String fieldPath) {
		if(dataSource == null || fieldPath == null || fieldPath.equals(""))
			return null;
		
		IObservable<?> observable = null;			
		InnerFieldObservable ifo = new InnerFieldObservable(fieldPath);
		if (ifo.createNodes(dataSource)) {
			observable = ifo;										
		} else {			
			Object rawField;
			try {
				rawField =  Binder.getSyntaxResolver().getFieldForModel(fieldPath, dataSource);
			} catch (SyntaxResolveException e) {
				BindingLog.exception("Utility.getObservableByFieldPath()", e);
				return null;
			}
			if (rawField instanceof IObservable<?>)
				observable = (IObservable<?>)rawField;
			else if (rawField!=null)
				observable= new ConstantObservable(rawField.getClass(), rawField);
		}	
		
		return observable;
	}
	
	public static Integer getClickedListPositon(IObservableCollection<?> items, Object[] args) {
		if( items == null || args.length != 3 || !(args[1] instanceof Integer))
			return null;

		Integer pos = (Integer) args[1];
		if (pos < 0 || pos > items.size())
			return null;
		
		return pos;
	}
	
	public static void ensureVisible(ListView listView, int pos) {
	    if (listView == null)
	        return;

	    if(pos < 0 || pos >= listView.getCount())
	        return;

	    int first = listView.getFirstVisiblePosition();
	    int last = listView.getLastVisiblePosition();

	    if (pos < first || last < 0) {
	        listView.setSelection(pos);
	        return;
	    }

	    if (pos >= last) {
	        listView.setSelection(1 + pos - (last - first));
	    }
	}
}

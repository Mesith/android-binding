package gueei.binding.v30.app;

import gueei.binding.AttributeBinder;
import gueei.binding.Binder.InflateResult;
import gueei.binding.v30.BinderV30;
import gueei.binding.v30.widget.PopupMenuBinderV30;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.PopupMenu;

public class BindingWidgetV30 {
	
	public static PopupMenu createAndBindPopupMenu(View view, int menuId, Object menuViewModel) {
		PopupMenu popup = new PopupMenu(view.getContext(), view);		
		
		PopupMenuBinderV30 menuBinder = new PopupMenuBinderV30();
		menuBinder.createPopupMenu(view, popup, menuId, menuViewModel);
		return popup;
	}

	public static Dialog createAndBindDialog(Context context, int layoutId, Object contentViewModel) {
		return createAndBindDialog(context, 0, layoutId, contentViewModel);
	}
	
	public static Dialog createAndBindDialog(Context context, int theme, int layoutId, Object contentViewModel) {
		Dialog dialog = new Dialog(context, theme);
		InflateResult result = BinderV30.inflateView(context, layoutId, null, false);
		dialog.setContentView(result.rootView);
		for(View v: result.processedViews){
			AttributeBinder.getInstance().bindView(context, v, contentViewModel);
		}			
        return dialog;                 
	}	
}

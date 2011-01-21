package com.gueei.android.binding.bindingProviders;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.gueei.android.binding.Binder;
import com.gueei.android.binding.BindingMap;
import com.gueei.android.binding.Command;
import com.gueei.android.binding.Utility;
import com.gueei.android.binding.ViewAttribute;
import com.gueei.android.binding.cursor.CursorAdapter;
import com.gueei.android.binding.listeners.OnItemClickedListenerMulticast;
import com.gueei.android.binding.listeners.OnItemSelectedListenerMulticast;
import com.gueei.android.binding.viewAttributes.ClickedIdViewAttribute;
import com.gueei.android.binding.viewAttributes.ClickedItemViewAttribute;
import com.gueei.android.binding.viewAttributes.GenericViewAttribute;
import com.gueei.android.binding.viewAttributes.ItemSourceViewAttribute;

public class AdapterViewProvider extends BindingProvider {

	@SuppressWarnings("unchecked")
	@Override
	public <Tv extends View> ViewAttribute<Tv, ?> createAttributeForView(
			View view, String attributeId) {
		if (!(view instanceof AdapterView))
			return null;
		try {
			if (attributeId.equals("adapter")) {
				// TODO: Can change to very specific class to avoid the
				// reflection methods
				ViewAttribute<AdapterView, Adapter> attr = new GenericViewAttribute(
						(AdapterView) view, "adapter", 
						AdapterView.class.getMethod("getAdapter"), 
						AdapterView.class.getMethod("setAdapter", Adapter.class));
				return (ViewAttribute<Tv, ?>) attr;
			} else if (attributeId.equals("selectedItem")) {
				ViewAttribute<AdapterView, Object> attr = new GenericViewAttribute(
						(AdapterView) view, "selectedItem", AdapterView.class
								.getMethod("getSelectedItem"), null);
				attr.setReadonly(true);
				Binder.getMulticastListenerForView
					(view, OnItemSelectedListenerMulticast.class).register(attr);
				return (ViewAttribute<Tv, ?>) attr;
			} else if (attributeId.equals("clickedItem")){
				ViewAttribute<AdapterView<?>, Object> attr = 
					new ClickedItemViewAttribute((AdapterView)view, "clickedItem");
				return (ViewAttribute<Tv, ?>) attr;
			} else if (attributeId.equals("clickedId")){
				ViewAttribute<AdapterView<?>, Long> attr = 
					new ClickedIdViewAttribute((AdapterView)view, "clickedId");
				return (ViewAttribute<Tv, ?>) attr;
			} else if (attributeId.equals("itemSource")){
				ItemSourceViewAttribute attr = 
					new ItemSourceViewAttribute((AdapterView)view, "itemSource");
				return (ViewAttribute<Tv, ?>) attr;
			}
		} catch (Exception e) {
			// Actually it should never reach this statement
		}
		return null;
	}

	@Override
	public void bind(View view, BindingMap map, Object model) {
		if (!(view instanceof AdapterView<?>)) return;
		bindViewAttribute(view, map, model, "selectedItem");
		bindViewAttribute(view, map, model, "clickedItem");
		bindViewAttribute(view, map, model, "clickedId");
		bindViewAttribute(view, map, model, "adapter");
		//bindViewAttribute(view, map, model, "itemSource");
		
		if (map.containsKey("itemSelected")){
			Command command = Utility.getCommandForModel(map.get("itemSelected"), model);
			if (command!=null){
				Binder
					.getMulticastListenerForView(view, OnItemSelectedListenerMulticast.class)
					.register(command);
			}
		}
		if (map.containsKey("itemClicked")){
			Command command = Utility.getCommandForModel(map.get("itemClicked"), model);
			if (command!=null){
				Binder
					.getMulticastListenerForView(view, OnItemClickedListenerMulticast.class)
					.register(command);
			}
		}
	}
}

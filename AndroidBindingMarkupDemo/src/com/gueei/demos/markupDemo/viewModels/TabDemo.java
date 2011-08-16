package com.gueei.demos.markupDemo.viewModels;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.gueei.demos.markupDemo.OptionsMenu;
import com.gueei.demos.markupDemo.Parceling;
import com.gueei.demos.markupDemo.R;
import com.gueei.demos.markupDemo.custom_actionbar.ActCustomAB;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.viewAttributes.tabHost.Tab;

/**
 * User: =ra=
 * Date: 16.08.11
 * Time: 15:14
 */
public class TabDemo {

	public ArrayListObservable<Tab> TabsArray = new ArrayListObservable<Tab>(Tab.class);
	public IntegerObservable TabWidth = new IntegerObservable(160);
    public IntegerObservable SelectedPosition = new IntegerObservable(1);
	//
	private Context mContext;

	public TabDemo(Context context) {
		mContext = context;
		initTabs();
	}

	private void initTabs() {
		Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon);
		//--
		Tab tab = new Tab();
		tab.Activity.set(ActCustomAB.class.getName());
		tab.Label.set("Custom View ActionBar");
		tab.Icon.set(drawable);
		TabsArray.add(tab);
		// --
		tab = new Tab();
		tab.Activity.set(Parceling.class.getName());
		tab.Label.set("Parceling");
		tab.Icon.set(drawable);
		TabsArray.add(tab);
		//--
		tab = new Tab();
		tab.Activity.set(OptionsMenu.class.getName());
		tab.Label.set("Options Menu");
		tab.Icon.set(drawable);
		TabsArray.add(tab);

	}
}

package com.gueei.android.binding;

import java.util.AbstractCollection;

public interface Observer {
	public <T> void onPropertyChanged(Observable<T> prop, T newValue, AbstractCollection<Object> initiators);
}

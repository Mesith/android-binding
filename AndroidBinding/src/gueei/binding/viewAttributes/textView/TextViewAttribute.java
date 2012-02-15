package gueei.binding.viewAttributes.textView;

import gueei.binding.Binder;
import gueei.binding.BindingType;
import gueei.binding.ViewAttribute;
import gueei.binding.listeners.TextWatcherMulticast;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class TextViewAttribute extends ViewAttribute<TextView, CharSequence> implements TextWatcher {

	private CharSequence mValue = null;
	private int selectionStart = -1;

	public TextViewAttribute(TextView view, String attributeName) {
		super(CharSequence.class, view, attributeName);
		if (view instanceof EditText) {
			Binder.getMulticastListenerForView(view, TextWatcherMulticast.class).registerWithHighPriority(this);
		}
	}

	@Override
	public CharSequence get() {
		return cloneCharSequence(getView().getText());
	}

	private CharSequence cloneCharSequence(CharSequence o) {
		return o.subSequence(0, o.length());
	}

	private boolean compareCharSequence(CharSequence a, CharSequence b) {
		boolean result = false;
		if (a != null) {
			result = a.equals(b);
		}
		return result;
	}

	@Override
	protected void doSetAttributeValue(Object newValue) {
		synchronized (this) {
			CharSequence nVal = "";
			if (null != newValue) {
				if (newValue instanceof CharSequence) {
					nVal = (CharSequence) newValue;
				} else {
					nVal = newValue.toString();
				}
			}
			if (!compareCharSequence(nVal, mValue)) {
				suppressChange = true;
				mValue = cloneCharSequence(nVal);
				getView().setText(cloneCharSequence(nVal));
				
				if (getView() instanceof EditText && selectionStart > 0) {
					((EditText)getView()).setSelection(selectionStart, selectionStart);
					selectionStart = -1;
				}
			}
		}
	}

	private boolean suppressChange = false;

	public void afterTextChanged(Editable arg0) {
	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		synchronized (this) {
			//Log.i("BinderV30", "onTextChagnged, mV=" + mValue + "\t arg0=" + arg0);
			if (compareCharSequence(mValue, arg0))
				return;
			if (!suppressChange) {
				mValue = cloneCharSequence(arg0);

				if (getView() instanceof EditText)
					selectionStart = ((EditText)getView()).getSelectionStart();
				
				this.notifyChanged();
			}
			suppressChange = false;
		}
	}

	@Override
	protected BindingType AcceptThisTypeAs(Class<?> type) {
		if (CharSequence.class.isAssignableFrom(type))
			return BindingType.TwoWay;
		if (type.isAssignableFrom(CharSequence.class))
			return BindingType.TwoWay;
		return BindingType.OneWay;
	}
}

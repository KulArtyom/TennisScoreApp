package com.kulart05gmail.tennisscore.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.kulart05gmail.tennisscore.R;


public class ConfirmActionDialog extends DialogFragment implements View.OnClickListener {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TAG = ConfirmActionDialog.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================
    private String mTitle;
    private ConfirmActionListener mListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    public static ConfirmActionDialog getInstance(String title, ConfirmActionListener listener) {
        ConfirmActionDialog dialog = new ConfirmActionDialog();
        dialog.setTitle(title);
        dialog.setListener(listener);
        return dialog;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setListener(ConfirmActionListener listener) {
        this.mListener = listener;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View root = inflater.inflate(R.layout.dialog_confirm_action, container, false);
        root.findViewById(R.id.btn_ok).setOnClickListener(this);
        root.findViewById(R.id.btn_cancel).setOnClickListener(this);

        ((TextView) root.findViewById(R.id.tv_title)).setText(mTitle);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                mListener.onConfirm();
                dismiss();
                break;
            case R.id.btn_cancel:
                mListener.onCancel();
                dismiss();
                break;
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public interface ConfirmActionListener {
        void onConfirm();

        void onCancel();
    }

}

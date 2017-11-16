package com.jvtao.phonelive.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;


import com.jvtao.phonelive.interf.DialogInterface;
import com.jvtao.phonlive.R;

/**
 * UI公共类
 */
public class LiveCommon {
    public static void showInputContentDialog(Context context, String title, final DialogInterface dialogInterface) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_set_room_pass);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        ((TextView) dialog.findViewById(R.id.tv_title)).setText(title);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.cancelDialog(view, dialog);
            }
        });
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.determineDialog(view, dialog);
            }
        });


    }

    public static void showIRtcDialog(Context context, String title, String content, final DialogInterface dialogInterface) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_show_rtcmsg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        ((TextView) dialog.findViewById(R.id.tv_title)).setText(title);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.cancelDialog(view, dialog);
            }
        });
        TextView textView = (TextView) dialog.findViewById(R.id.et_input);
        textView.setText(content);
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.determineDialog(view, dialog);
            }
        });
    }

    public static void showMainTainDialog(Context context, String content) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_maintain);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        ((TextView) dialog.findViewById(R.id.tv_content)).setText(content);
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

}

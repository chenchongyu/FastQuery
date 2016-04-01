package net.runningcode.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Administrator on 2016/1/15.
 */
public class DialogUtils {
    public static void showShortToast(Context context,String content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
    public static void showShortToast(Context context,int content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
    public static void showLongToast(Context context,String content){
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }
    public static void showLongToast(Context context,int content){
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    public static MaterialDialog alertDialog(Context context,String title,String content, final View.OnClickListener onClickListener){
        final MaterialDialog materialDialog = new MaterialDialog(context).setTitle(title).setMessage(content);
        materialDialog.setPositiveButton(android.R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
                onClickListener.onClick(view);
            }
        });
        return materialDialog;
    }

    public static MaterialDialog confirmDialog(Context context,String title,String content, final View.OnClickListener onClickListener){
        final MaterialDialog materialDialog = new MaterialDialog(context).setTitle(title).setMessage(content);
        materialDialog.setPositiveButton(android.R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
                onClickListener.onClick(view);
            }
        }).setNegativeButton(android.R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
            }
        });
        return materialDialog;
    }

}

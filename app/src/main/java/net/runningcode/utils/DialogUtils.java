package net.runningcode.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.runningcode.R;
import net.runningcode.RunningCodeApplication;


/**
 * Created by Administrator on 2016/1/15.
 */
public class DialogUtils {
    public static void showShortToast(Context context,String content){
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
    public static void showShortToast(String content){
        Toast.makeText(RunningCodeApplication.getInstance(), content, Toast.LENGTH_SHORT).show();
    }
    public static void showShortToast(int content){
        Toast.makeText(RunningCodeApplication.getInstance(), content, Toast.LENGTH_SHORT).show();
    }
    public static void showLongToast(Context context,String content){
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }
    public static void showLongToast(Context context,int content){
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    public static AlertDialog alertDialog(Context context,String title,String content, DialogInterface.OnClickListener onClickListener){

        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,onClickListener)
                .create();

    }
//
    public static AlertDialog confirmDialog(Context context, String title, String content,
                                            final DialogInterface.OnClickListener onOKClickListener,
                                            final DialogInterface.OnClickListener onCancleListener){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(android.R.string.ok,onOKClickListener)
                .setNegativeButton(android.R.string.cancel,onCancleListener)
                .create();
    }

    public static AlertDialog loadingDialog(Context context,int content){
        /*ProgressDialog dialog = new ProgressDialog(context,android.R.style.Widget_Material_Light_ProgressBar_Inverse);
        dialog.setMessage(context.getString(content));
        dialog.setProgressStyle(STYLE_SPINNER);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
//        dialog.set
        return dialog;*/

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();

        dialog.setCanceledOnTouchOutside(false);
        View view=View.inflate(context, R.layout.dialog_loading, null);

        TextView msgTv=(TextView) view.findViewById(R.id.msg);
        msgTv.setText(content);
        dialog.setView(view,0,0,0,0);
        return  dialog;
    }



}

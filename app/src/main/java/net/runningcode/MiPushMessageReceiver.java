package net.runningcode;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import net.runningcode.constant.Constants;
import net.runningcode.utils.CommonUtil;
import net.runningcode.utils.L;
import net.runningcode.utils.SPUtils;

import java.util.List;
/**
 * 1、PushMessageReceiver是个抽象类，该类继承了BroadcastReceiver。
 * 2、需要将自定义的DemoMessageReceiver注册在AndroidManifest.xml文件中 <receiver
 * android:exported="true"
 * android:name="com.xiaomi.mipushdemo.DemoMessageReceiver"> <intent-filter>
 * <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" /> </intent-filter>
 * <intent-filter> <action android:name="com.xiaomi.mipush.ERROR" />
 * </intent-filter> </receiver>
 * 3、DemoMessageReceiver的onCommandResult方法用来接收客户端向服务器发送命令后的响应结果
 * 4、DemoMessageReceiver的onReceiveMessage方法用来接收服务器向客户端发送的消息
 * 5、onReceiveMessage和onCommandResult方法运行在非UI线程中
 *
 * @author wangkuiwei
 */
public class MiPushMessageReceiver extends PushMessageReceiver {
	private final static String LOG = "mipush";
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message){
        L.i("onReceivePassThroughMessage is called. " + message.toString());
    }

    @Override
    public void onNotificationMessageArrived(final Context context, MiPushMessage message){
        L.i("onNotificationMessageArrived is called. " + message);


    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message){
        L.i("onNotificationMessageClicked is called. " + message.toString());
        String type = message.getContent();
        if (TextUtils.equals(type, Constants.TYPE_OPEN)){
            CommonUtil.openMarket(context);
        }

    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        L.i("onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log = "";
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            Log.i(LOG, "注册成功！"+cmdArg1);
            String channel = SPUtils.getInstance(null).getString(Constants.KEY_CHANNEL,Constants.DEFAULT_CHANNEL);
            MiPushClient.subscribe(context, channel, null);
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            Log.i(LOG, "别名设置成功！"+cmdArg1);
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                log = context.getString(R.string.unset_alias_success, cmdArg1);
            } else {
//                log = context.getString(R.string.unset_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                log = context.getString(R.string.subscribe_topic_success, cmdArg1);
            } else {
//                log = context.getString(R.string.subscribe_topic_fail, message.getReason());
            }
            Log.i(LOG, "设置topic成功！");
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                Log.i(LOG, "取消订阅topic成功！"+command);
            } else {
                 Log.i(LOG, "取消订阅topic失败！"+command);
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
//                log = context.getString(R.string.set_accept_time_success, cmdArg1, cmdArg2);

                Message msg = Message.obtain();
                msg.what = 1;
                msg.arg1 = (cmdArg1.equals(cmdArg2))?0:1;
//                DemoApplication.getHandler().sendMessage(msg);
            } else {
//                log = context.getString(R.string.set_accept_time_fail, message.getReason());
            }
        } else {
            log = message.getReason();
            Log.i(LOG, "设置失败！"+log);
        }

        Message msg = Message.obtain();
        msg.obj = log;
//        DemoApplication.getHandler().sendMessage(msg);
    }
}

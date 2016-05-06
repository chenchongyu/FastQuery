package net.runningcode.dao;

import net.runningcode.utils.L;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Administrator on 2016/5/4.
 */
public class Dao {
    public static void saveAsync(final RealmObject express){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(express);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                L.e("保存错误："+error);
            }
        });
    }

    public RealmObject save(RealmObject exp){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmObject express = realm.copyToRealm(exp);
        realm.commitTransaction();
        return express;
    }

    public static RealmResults getAllList(Class cls){
        Realm realm = Realm.getDefaultInstance();
        RealmResults list = realm.where(cls).findAll();
        return list;
    }
}

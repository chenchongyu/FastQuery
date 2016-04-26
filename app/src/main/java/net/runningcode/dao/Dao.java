package net.runningcode.dao;

import android.database.sqlite.SQLiteDatabase;

import net.runningcode.RunningCodeApplication;
import net.runningcode.utils.L;

/**
 * Created by Administrator on 2016/4/12.
 */
public class Dao {
    private DaoSession daoSession;
    private Dao(){
        initDb();
    }
    private static class SingletonHolder{
        private static final Dao INSTANCE = new Dao();
    }


    public static DaoSession getInstance(){
        return SingletonHolder.INSTANCE.daoSession;
    }

    private void initDb() {
        if (daoSession == null){
            L.i("initDb");
        }
        DaoMaster.OpenHelper openHelper = new DaoMaster.OpenHelper(RunningCodeApplication.getInstance(), "quick-query", null) {
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };

        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
        daoSession = daoMaster.newSession();
    }
}

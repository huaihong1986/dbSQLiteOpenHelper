package com.test;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.lang.reflect.Field;

public class DBActivity extends Activity {

    private ToDoDB myToDoDB;
    private Cursor myCursor;
    private ListView myListView;
    private SimpleCursorAdapter mAdapter;
    private EditText myEditText;
    private int _id;
    protected final static int MENU_ADD = Menu.FIRST;
    protected final static int MENU_EDIT = Menu.FIRST + 1;
    protected final static int MENU_DELETE = Menu.FIRST + 2;
    protected final static int MENU_QUERY = Menu.FIRST + 3;
    private final static String TAG = "DBActivity";

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case MENU_ADD:
                this.addTodo();
                break;
            case MENU_EDIT:
                this.editTodo();
                break;
            case MENU_DELETE:
                this.deleteTodo();
                break;
            case MENU_QUERY:
                this.queryTodo();
                break;
        }
        return true;
    }

    //force to show overflow menu in actionbar
    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_ADD, 0, R.string.strAddButton);
        menu.add(Menu.NONE, MENU_EDIT, 0, R.string.strEditButton);
        menu.add(Menu.NONE, MENU_DELETE, 0, R.string.strDeleteButton);
        menu.add(Menu.NONE, MENU_QUERY, 0, R.string.strQueryButton);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;

    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        myListView = (ListView) this.findViewById(R.id.myListView);
        myEditText = (EditText) this.findViewById(R.id.myEditText);
        getOverflowMenu();
        myToDoDB = new ToDoDB(this);
        /* 取得DataBase里的数据 */
        myCursor = myToDoDB.select();
        while (myCursor.moveToNext()) {
            //使用GetString获取列中的值。参数为使用cursor.getColumnIndex("name")获取的序号。
            String nameString = myCursor.getString(myCursor.getColumnIndex("_id"));

            Log.v(TAG, "onCreate" + nameString);

        }

	    /* new SimpleCursorAdapter并将myCursor传入，
	       显示数据的字段为todo_text */
        mAdapter =
                new SimpleCursorAdapter
                        (this, R.layout.list, myCursor, new String[]
                                {ToDoDB.FIELD_id, ToDoDB.FIELD_TEXT}, new int[]
                                {R.id.listTextView1, R.id.listTextView2});
        myListView.setAdapter(mAdapter);

	    /* 将myListView添加OnItemClickListener */
        myListView.setOnItemClickListener
                (new AdapterView.OnItemClickListener() {

                    public void onItemClick
                            (AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	        /* 将myCursor移到所点击的值 */
                        myCursor.moveToPosition(arg2);
	        /* 取得字段_id的值 */
                        _id = myCursor.getInt(0);
	        /* 取得字段todo_text的值 */
                        myEditText.setText(myCursor.getString(1));
                    }

                });
        myListView.setOnItemSelectedListener
                (new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected
                            (AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	        /* getSelectedItem所取得的是SQLiteCursor */
                        SQLiteCursor sc = (SQLiteCursor) arg0.getSelectedItem();
                        _id = sc.getInt(0);
                        myEditText.setText(sc.getString(1));
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
    }

    private void addTodo() {
        if (myEditText.getText().toString().equals(""))
            return;
	    /* 添加数据到数据库 */
        myToDoDB.insert(myEditText.getText().toString());
	    /* 重新查询 */
        myCursor = myToDoDB.select();
        mAdapter.changeCursor(myCursor);
	    /* 重新整理myListView */
        myListView.invalidateViews();
        myEditText.setText("");
        _id = 0;
    }

    private void editTodo() {
        if (myEditText.getText().toString().equals(""))
            return;
	    /* 修改数据 */
        myToDoDB.update(_id, myEditText.getText().toString());
        myCursor = myToDoDB.select();
        mAdapter.changeCursor(myCursor);
        myListView.invalidateViews();
        myEditText.setText("");
        _id = 0;
    }

    private void deleteTodo() {
        if (_id == 0)
            return;
	    /* 删除数据 */
        myToDoDB.delete(_id);
        myCursor = myToDoDB.select();
        mAdapter.changeCursor(myCursor);
        myListView.invalidateViews();
        myEditText.setText("");
        _id = 0;
    }

    private void queryTodo() {
        if (myEditText.getText().toString().equals(""))
            return;
	    /* 查询数据 */
        myCursor = myToDoDB.select(myEditText.getText().toString());
        while (myCursor.moveToNext()) {
            //使用GetString获取列中的值。参数为使用cursor.getColumnIndex("name")获取的序号。
            String nameString = myCursor.getString(myCursor.getColumnIndex("_id"));

            Log.v(TAG, "queryTodo" + nameString);

        }
        mAdapter.changeCursor(myCursor);
        myListView.invalidateViews();
        myEditText.setText("");
        _id = 0;
    }
}
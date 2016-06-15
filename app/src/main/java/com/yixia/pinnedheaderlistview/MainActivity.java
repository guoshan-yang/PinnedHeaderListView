package com.yixia.pinnedheaderlistview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    PinnedHeaderListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (PinnedHeaderListView) findViewById(R.id.listView);

        listView.setData(getData());

    }


    private ArrayList<ChooseCountryBean> getData(){

        ArrayList<ChooseCountryBean> beans = new ArrayList<>();

        for (int i = 1 ; i < 11; i++){
            ChooseCountryBean bean = new ChooseCountryBean();
            bean.setCode("A"+i);
            bean.setCountry("A"+i);
            bean.setSort("常用");
            beans.add(bean);
        }

        for (int i = 1 ; i < 11; i++){
            ChooseCountryBean bean = new ChooseCountryBean();
            bean.setCode("A"+i);
            bean.setCountry("A"+i);
            bean.setSort("AA");
            beans.add(bean);
        }

        for (int i = 1 ; i < 11; i++){
            ChooseCountryBean bean = new ChooseCountryBean();
            bean.setCode("B"+i);
            bean.setCountry("B"+i);
            bean.setSort("BB");
            beans.add(bean);
        }

        for (int i = 1 ; i < 11; i++){
            ChooseCountryBean bean = new ChooseCountryBean();
            bean.setCode("C"+i);
            bean.setCountry("C"+i);
            bean.setSort("CC");
            beans.add(bean);
        }

        for (int i = 1 ; i < 11; i++){
            ChooseCountryBean bean = new ChooseCountryBean();
            bean.setCode("D"+i);
            bean.setCountry("D"+i);
            bean.setSort("DD");
            beans.add(bean);
        }

        for (int i = 1 ; i < 11; i++){
            ChooseCountryBean bean = new ChooseCountryBean();
            bean.setCode("E"+i);
            bean.setCountry("E"+i);
            bean.setSort("EE");
            beans.add(bean);
        }

        for (int i = 1 ; i < 11; i++){
            ChooseCountryBean bean = new ChooseCountryBean();
            bean.setCode("F"+i);
            bean.setCountry("F"+i);
            bean.setSort("FF");
            beans.add(bean);
        }

        return beans;
    }
}

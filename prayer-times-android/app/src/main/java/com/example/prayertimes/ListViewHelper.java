package com.example.prayertimes;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

// source: https://stackoverflow.com/questions/52217728/listview-shows-only-one-item-in-a-scrollview
public class ListViewHelper {
    public static void adjustListViewSize(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter != null) {
            int totalHeight = 0;

            //setting list adapter in loop to get final size
            for (int i = 0; i < adapter.getCount(); i++) {
                View listItem = adapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            //setting listview items in adapter
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() *
                    (adapter.getCount() - 1));
            listView.setLayoutParams(params);


        } else {
            return;
        }
    }
}
package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/*
 Simple month grid adapter (6 rows x 7 cols). Shows day number and event count (if any).
 */
public class CalendarAdapter extends BaseAdapter {

    private final Context context;
    private final Calendar monthCalendar; // first day set to month start
    private final Map<String, List<Event>> eventsMap;
    private final LayoutInflater inflater;
    private final int totalCells = 42; // 6 weeks

    public CalendarAdapter(Context context, Calendar monthCalendar, Map<String, List<Event>> eventsMap) {
        this.context = context;
        this.monthCalendar = (Calendar) monthCalendar.clone();
        this.eventsMap = eventsMap;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return totalCells;
    }

    /**
     * Return the date key (yyyy-MM-dd) for this position so callers (fragment) can know the day.
     */
    @Override
    public Object getItem(int position) {
        return dayKeyForPosition(position);
    }

    private String dayKeyForPosition(int position) {
        Calendar cal = (Calendar) monthCalendar.clone();
        int firstDayOfWeek = cal.getFirstDayOfWeek();
        int dayOfWeekIndex = cal.get(Calendar.DAY_OF_WEEK);
        // number of blank cells before day 1
        int offset = (dayOfWeekIndex - firstDayOfWeek + 7) % 7;
        cal.add(Calendar.DAY_OF_MONTH, position - offset);
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
        return df.format(cal.getTime());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_calendar_day, parent, false);
            vh = new ViewHolder();
            vh.dayNum = convertView.findViewById(R.id.dayNumber);
            vh.eventInfo = convertView.findViewById(R.id.eventInfo);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        String key = dayKeyForPosition(position);
        // parse key to determine if belongs to current month
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
        try {
            java.util.Date dt = df.parse(key);
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            int cellMonth = c.get(Calendar.MONTH);
            int currentMonth = monthCalendar.get(Calendar.MONTH);
            vh.dayNum.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
            if (cellMonth != currentMonth) {
                // dim days outside current month
                vh.dayNum.setAlpha(0.4f);
                vh.eventInfo.setAlpha(0.4f);
            } else {
                vh.dayNum.setAlpha(1f);
                vh.eventInfo.setAlpha(1f);
            }
            List<Event> list = eventsMap.get(key);
            if (list != null && !list.isEmpty()) {
                vh.eventInfo.setText(list.size() + " event" + (list.size() > 1 ? "s" : ""));
                vh.eventInfo.setVisibility(View.VISIBLE);
            } else {
                vh.eventInfo.setText("");
                vh.eventInfo.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            vh.dayNum.setText("");
            vh.eventInfo.setText("");
        }

        return convertView;
    }

    static class ViewHolder {
        TextView dayNum;
        TextView eventInfo;
    }
}

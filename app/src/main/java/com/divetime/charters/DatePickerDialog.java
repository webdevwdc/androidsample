package com.divetime.charters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.divetime.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.stacktips.view.CalendarListener;
import com.stacktips.view.CustomCalendarView;
import com.stacktips.view.DayDecorator;
import com.stacktips.view.DayView;
import com.stacktips.view.utils.CalendarUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_SINGLE;

/**
 * Created by android on 7/6/17.
 */

public class DatePickerDialog extends Dialog implements OnDateSelectedListener{

    DialogBuilder dialogBuiler;
    CustomCalendarView calendarView;

    String selected_date="";
    String dayOfTheWeek="";

    public DatePickerDialog(Context context, int themeResId) {
        super(context, themeResId);

        setContentView(R.layout.date_picker);
        Window window = getWindow();
        window.getAttributes().windowAnimations = R.style.dialog_animation;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
    }

    private void setDynamicLayout() {
        Dialog mView = DatePickerDialog.this;
       /* final MaterialCalendarView calendarView = (MaterialCalendarView) mView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(this);
//        calendarView.state().edit()
//                .setMinimumDate(Calendar.getInstance())
////                .setMaximumDate(CalendarDay.from(2016, 5, 12))
////                .setCalendarDisplayMode(CalendarMode.WEEKS)
//                .commit();
        ImageView btn_getDate = (ImageView) mView.findViewById(R.id.btn_getDate);



        calendarView.setSelectionMode(SELECTION_MODE_SINGLE);*/

        calendarView = (CustomCalendarView) findViewById(R.id.calendarView);


        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

        //Show monday as first date of week
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);

        //call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar);

        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                if (!CalendarUtils.isPastDay(date)) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                    selectedDateTv.setText("Selected date is " + df.format(date));
                    selected_date=df.format(date);

                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
//                    Date d = new Date();
                    dayOfTheWeek = sdf.format(date);

                    getDialogBuiler().submitListner.onDialogSubmit(selected_date);

                    dismiss();

//                    if(dayOfTheWeek.equalsIgnoreCase("Saturday") || dayOfTheWeek.equalsIgnoreCase("Sunday"))
//                    {
//                        Constants.showAlertDialog2(DatePickerActivity.this,getResources().getString(R.string.dialog_title),"Please select day from Monday to Friday !!");
//                        selected_date="";
//                    }


                } else {
                    selected_date="";
//                    Constants.showAlertDialog2(DatePickerActivity.this,getResources().getString(R.string.dialog_title),"Past Date not Allowed");
//                    selectedDateTv.setText("Selected date is disabled!");
                }
            }

            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
//                Toast.makeText(DatePickerActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });


        //adding calendar day decorators
        List<DayDecorator> decorators = new ArrayList<>();
        decorators.add(new DisabledColorDecorator());
        calendarView.setDecorators(decorators);
        calendarView.refreshCalendar(currentCalendar);
    }

    private class DisabledColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            if (CalendarUtils.isPastDay(dayView.getDate())) {
                int color = Color.parseColor("#e4e4e4");
                Date day=  dayView.getDate();
                Log.d("DAYYY ",day.toString());
//                dayView.setBackgroundColor(color);
                dayView.setTextColor(color);

                String outputPattern = "dd MMM yyyy";
                SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                String str = outputFormat.format(day);
                Log.d("DAYYY ",str);
                if(str.contains("Nov"))
                    Log.d("DAYYY ",str);

//                for(int i=0;i<al_events.size();i++) {
//                    String dat = al_events.get(i);
//                    if (dat.equalsIgnoreCase(str)) {
//                        dayView.setBackgroundColor(color);
//                        dayView.setTextColor(Color.parseColor("#FAFAFA"));
//                    }
//                }

            }
        }
    }

    public DialogBuilder getDialogBuiler() {
        return dialogBuiler;
    }

    public void setDialogBuidler(DialogBuilder dialogBuiler) {
        this.dialogBuiler = dialogBuiler;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//        Toast.makeText(getContext(),date.getMonth()+1+"-"+date.getDay()+"-"+date.getYear(), Toast.LENGTH_SHORT).show();
        if(getDialogBuiler().submitListner!=null){
            int month=date.getMonth();
            month++;
            getDialogBuiler().submitListner.onDialogSubmit(date.getYear()+"-"+month+"-"+date.getDay());
            dismiss();
        }
//        dismiss();

        date.getDay();
    }

    public static class DialogBuilder {

        private Context context;
        private DialogSubmitListner submitListner;

        public DialogBuilder setSubmitListner(DialogSubmitListner submitListner) {
            this.submitListner = submitListner;
            return this;
        }

        public DialogBuilder setContext(Context context) {
            this.context = context;
            return this;
        }

        public void showDialog() {
            DatePickerDialog dialogObj = new DatePickerDialog(this.context, R.style.FullHeightDialog);
            dialogObj.setDynamicLayout();
            dialogObj.setDialogBuidler(this);
            dialogObj.show();
        }
    }

    public interface DialogSubmitListner {
        void onDialogSubmit(String date);
    }
}

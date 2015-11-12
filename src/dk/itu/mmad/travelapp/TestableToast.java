package dk.itu.mmad.travelapp;

import android.content.Context;
import android.widget.Toast;

public class TestableToast extends Toast {

        public TestableToast(Context context) {
                super(context);
        }

        private static String msg;

        public static String getLastToastMsg(){
                return msg;
        }
        public static void clearLastToastMsg(){
                msg = null;
        }

        public static Toast makeText (Context context, CharSequence text, int duration){
                msg = text.toString();
                return Toast.makeText(context, text, duration);
        }

}
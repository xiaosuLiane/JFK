package com.example.jfk;

import android.content.Context;
import android.widget.TextView;

public class ddd extends TextView {
    private int TypeID;
    public ddd(Context context) {
        super(context);
    }
    public void setTypeID(int ID){ this.TypeID = ID;}
    public int getTypeID(){return this.TypeID;}
}

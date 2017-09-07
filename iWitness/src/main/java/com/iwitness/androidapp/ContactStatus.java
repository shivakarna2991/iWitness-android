package com.iwitness.androidapp;

import java.math.*;

public enum ContactStatus{
    PENDING (1),
    ACCEPTED (2),
    DECLINED (4);

    int _value;
    ContactStatus(int value) {
        _value = value;
    }

    public int getId(){return _value;}

    public boolean Compare(int i){return (_value & i) > 0;}

    public static ContactStatus fromValue(int id)
    {
        ContactStatus[] As = ContactStatus.values();
        for (ContactStatus A : As) {
            if (A.Compare(id))
                return A;
        }
        return ContactStatus.PENDING;
    }

    public static ContactStatus fromValue(BigInteger id)
    {
        ContactStatus[] As = ContactStatus.values();
        for (ContactStatus A : As) {
            if (A.Compare(id.intValue()))
                return A;
        }
        return ContactStatus.PENDING;
    }
}

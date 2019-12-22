package com.glebworx.pomodoro.util;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;

import java.util.Comparator;

public class TimestampFieldComparator implements Comparator<DocumentChange> {

    private String fieldName;

    public TimestampFieldComparator(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public int compare(DocumentChange o1, DocumentChange o2) {
        Timestamp date1 = (Timestamp) o1.getDocument().get(fieldName);
        Timestamp date2 = (Timestamp) o2.getDocument().get(fieldName);
        return date2 == null || date1 == null ? 1 : date2.compareTo(date1);
    }

}

package com.insomniac.eventmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

/**
 * Created by Sanjeev on 1/17/2018.
 */

public class EventManagerAddFragment extends Fragment {

    private static final String TAG = EventManagerAddFragment.class.getSimpleName();
    private FirebaseFirestore mFirebaseFirestoreDb;
    private boolean isEdit;
    private String docId;
    private Button mEventButton;
    private Event mEvent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(EventManagerActivity.class.getSimpleName(),"onCreate2");
        View view = inflater.inflate(R.layout.fragment_event_manager_add,container,false);
        mEventButton = (Button) view.findViewById(R.id.add_event);
        mEvent = null;
        if(getArguments() != null){
            mEvent = getArguments().getParcelable("event");
            ((TextView)view.findViewById(R.id.add_tv)).setText(R.string.event_edit);
        }
        if(mEvent != null){
            ((TextView) view.findViewById(R.id.event_name_a)).setText(mEvent.getName());
            ((TextView) view.findViewById(R.id.event_type_a)).setText(mEvent.getType());
            ((TextView) view.findViewById(R.id.event_place_a)).setText(mEvent.getPlace());
            ((TextView) view.findViewById(R.id.event_start_time_a)).setText(mEvent.getStartTime());
            ((TextView) view.findViewById(R.id.event_end_time_a)).setText(mEvent.getEndTime());

            mEventButton.setText("Edit Event");
            isEdit = true;
            docId = mEvent.getId();
        }

        mFirebaseFirestoreDb = FirebaseFirestore.getInstance();

        mEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEdit){
                    addEvent();
                }else {
                    updateEvent();
                }
            }
        });

        return view;
    }

    private void addEvent(){
        Event event = createEvent();
        addDocumentToCollection(event);
    }

    private void updateEvent(){
        Event event = createEvent();
        updateDocumentToCollection(event);
    }

    private Event createEvent(){
        final Event event = new Event();
        event.setName(((TextView)getActivity()
                .findViewById(R.id.event_name_a)).getText().toString());
        event.setPlace(((TextView)getActivity()
                .findViewById(R.id.event_place_a)).getText().toString());
        event.setType(((TextView)getActivity()
                .findViewById(R.id.event_type_a)).getText().toString());
        event.setStartTime(((TextView)getActivity()
                .findViewById(R.id.event_start_time_a)).getText().toString());
        event.setEndTime(((TextView)getActivity()
                .findViewById(R.id.event_end_time_a)).getText().toString());

        return event;
    }

    private void addDocumentToCollection(Event event){
        mFirebaseFirestoreDb.collection("events")
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Event document added - id: "
                                + documentReference.getId());
                        restUi();
                        Toast.makeText(getActivity(),
                                "Event document has been added",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding event document", e);
                        Toast.makeText(getActivity(),
                                "Event document could not be added",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateDocumentToCollection(Event event){
        mFirebaseFirestoreDb.collection("events")
                .document(docId)
                .set(event, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Event document updated ");
                        Toast.makeText(getActivity(),
                                "Event document has been updated",
                                Toast.LENGTH_SHORT).show();
                        showEventScreen();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding event document", e);
                        Toast.makeText(getActivity(),
                                "Event document could not be added",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void restUi(){
        ((TextView)getActivity()
                .findViewById(R.id.event_name_a)).setText("");
        ((TextView)getActivity()
                .findViewById(R.id.event_type_a)).setText("");
        ((TextView)getActivity()
                .findViewById(R.id.event_place_a)).setText("");
        ((TextView)getActivity()
                .findViewById(R.id.event_start_time_a)).setText("");
        ((TextView)getActivity()
                .findViewById(R.id.event_end_time_a)).setText("");
    }

    private void showEventScreen(){
        Intent intent = new Intent(getActivity(),EventManagerActivity.class);
        startActivity(intent);
    }
}

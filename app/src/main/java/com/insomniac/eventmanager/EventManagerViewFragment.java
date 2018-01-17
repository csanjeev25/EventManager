package com.insomniac.eventmanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sanjeev on 1/17/2018.
 */

public class EventManagerViewFragment extends Fragment {

    private final static String TAG = EventManagerViewFragment.class.getSimpleName();
    private FirebaseFirestore mFirebaseFirestoreDb;
    private RecyclerView mEventRecyclerView;
    private List<Event> mEventList = new ArrayList<>();
    private EventsAdapter mEventsAdapter;
    private Button mViewEventsButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_manager_view,container,false);

        mFirebaseFirestoreDb = FirebaseFirestore.getInstance();
        mViewEventsButton = (Button) view.findViewById(R.id.view_event);
        mViewEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewEvents();
            }
        });

        mEventRecyclerView = (RecyclerView) view.findViewById(R.id.events_lst);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mEventRecyclerView.setLayoutManager(linearLayoutManager);
        mEventRecyclerView.addItemDecoration(new DividerItemDecoration(mEventRecyclerView.getContext(),linearLayoutManager.getOrientation()));

        return view;
    }

    private void viewEvents(){
        String eventType = ((TextView) getActivity()
                .findViewById(R.id.event_type_v)).getText().toString();
        getDocumentsFromCollection(eventType);
    }

    private void getDocumentsFromCollection(String eventType){
        mFirebaseFirestoreDb.collection("events")
                .whereEqualTo("type",eventType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<Event> events = new ArrayList<>();

                            for(DocumentSnapshot documentSnapshot : task.getResult()){
                                Event event = documentSnapshot.toObject(Event.class);
                                event.setId(documentSnapshot.getId());
                                events.add(event);

                                setUpAdapter(events);
                            }
                        }else
                            Toast.makeText(getActivity(),"Error + " + task.getException(),Toast.LENGTH_LONG).show();
                    }
                });

        mFirebaseFirestoreDb.collection("events")
                .whereEqualTo("type",eventType)
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        for(DocumentChange documentChange : documentSnapshots.getDocumentChanges()){
                            Event event = documentChange.getDocument().toObject(Event.class);
                            String oldId = Integer.toString(documentChange.getOldIndex());
                            for(int i = 0;i < mEventList.size();i++){
                                if(mEventList.get(i).getId().equals(oldId)){
                                    mEventList.remove(i);
                                    break;
                                }
                            }
                            event.setId(Integer.toString(documentChange.getNewIndex()));
                            mEventList.add(event);
                        }
                    }
                });

    }

    private void setUpAdapter(List<Event> events){
        mEventList = events;
        if(mEventsAdapter == null){
            mEventsAdapter = new EventsAdapter(mEventList,mFirebaseFirestoreDb);
            mEventRecyclerView.setAdapter(mEventsAdapter);
        }else
            mEventsAdapter.updateAdapter(mEventList);
    }

    private class EventsHolder extends RecyclerView.ViewHolder{

        public TextView mNameTextView;
        public TextView mPlaceTextView;
        public TextView mStartTimeTextView;
        public Button mEditButton;
        public Button mDeleteButton;
        private Event mEvent;


        public EventsHolder(View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.name_tv);
            mPlaceTextView = (TextView) itemView.findViewById(R.id.place_tv);
            mStartTimeTextView = (TextView) itemView.findViewById(R.id.start_time_tv);
            mEditButton = itemView.findViewById(R.id.edit_event_b);
            mDeleteButton = itemView.findViewById(R.id.delete_event_b);
        }


        private void bindView(Event event){
            mEvent = event;
            mNameTextView.setText(event.getName());
            mPlaceTextView.setText(event.getPlace());
            mStartTimeTextView.setText(event.getStartTime());
        }

        private void editEventFragment(Event event){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            Bundle args = new Bundle();
            args.putParcelable("event",event);

            EventManagerAddFragment eventManagerAddFragment = new EventManagerAddFragment();
            eventManagerAddFragment.setArguments(args);

            fragmentManager.beginTransaction().replace(R.id.events_content,eventManagerAddFragment).commit();
        }

        private void deleteEvent(String docId,final int position){
            mFirebaseFirestoreDb.collection("events").document(docId).delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mEventList.remove(position);
                            mEventsAdapter.notifyItemRemoved(position);
                            mEventsAdapter.notifyItemRangeChanged(position,mEventList.size());
                            Toast.makeText(getActivity(),
                                    "Event document has been deleted",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private class EventsAdapter extends RecyclerView.Adapter<EventsHolder>{

        private List<Event> mEvents;
        private FirebaseFirestore mFirebaseFirestore;

        public EventsAdapter(List<Event> events,FirebaseFirestore firebaseFirestore){
            mEvents = events;
            mFirebaseFirestore = firebaseFirestore;
        }

        @Override
        public EventsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.event_item,parent,false);
            return new EventsHolder(view);
        }

        @Override
        public void onBindViewHolder(EventsHolder holder, int position) {
            final int itemPos = position;
            final Event event = mEvents.get(position);
            final EventsHolder eventsHolder = holder;
            holder.bindView(event);

            holder.mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   eventsHolder.editEventFragment(event);
                }
            });

            eventsHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventsHolder.deleteEvent(event.getId(), itemPos);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }

        public void updateAdapter(List<Event> events){
            mEvents = events;
            notifyDataSetChanged();
        }
    }
}

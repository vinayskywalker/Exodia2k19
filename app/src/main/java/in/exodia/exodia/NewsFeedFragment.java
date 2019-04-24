package in.exodia.exodia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class NewsFeedFragment extends Fragment {
    private FirebaseFirestore mfirebasefirestore;
    private static final String TAG="Firestore";
    private List<Users> usersList;
    private LinearLayoutManager mLinearLayoutManager;
    private UsersListAdapter usersListAdapter;
    RecyclerView mmainlist;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_newsfeed,container,false);

        usersList=new ArrayList<>();
        usersListAdapter=new UsersListAdapter(usersList);


        mmainlist=(RecyclerView) root.findViewById(R.id.recycler);
        mmainlist.setHasFixedSize(true);
        mLinearLayoutManager=new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);
        mmainlist.setLayoutManager(mLinearLayoutManager);
        mmainlist.setAdapter(usersListAdapter);



        this.mfirebasefirestore= FirebaseFirestore.getInstance();
        mfirebasefirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.d(TAG,"Error");
                    return;
                }
                assert queryDocumentSnapshots != null;
                for(DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType()==DocumentChange.Type.ADDED){
                        Users users=doc.getDocument().toObject(Users.class);
                        usersList.add(users);
                        usersListAdapter.notifyDataSetChanged();
                    }

                    else if(doc.getType()==DocumentChange.Type.MODIFIED){
                        String docID=doc.getDocument().getId();
                        Users changeduser=doc.getDocument().toObject(Users.class);
                        if(doc.getNewIndex()==doc.getOldIndex()){
                            usersList.set(doc.getOldIndex(),changeduser);
                            usersListAdapter.notifyItemChanged(doc.getOldIndex());
                        }
                        else{
                            usersList.remove(doc.getOldIndex());
                            usersList.add(doc.getNewIndex(),changeduser);
                            usersListAdapter.notifyItemMoved(doc.getOldIndex(),doc.getNewIndex());
                        }
                    }
                    else if(doc.getType()==DocumentChange.Type.REMOVED){
                        usersList.remove(doc.getOldIndex());
                        usersListAdapter.notifyItemRemoved(doc.getOldIndex());
                    }
                }
            }
        });
        return root;
    }
}

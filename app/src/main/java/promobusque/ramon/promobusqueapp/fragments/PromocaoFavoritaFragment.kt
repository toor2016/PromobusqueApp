package promobusque.ramon.promobusqueapp.fragments

import android.content.Context
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import promobusque.ramon.promobusqueapp.R
import promobusque.ramon.promobusqueapp.modelos.PromocaoFavorita
import promobusque.ramon.promobusqueapp.modelos.TAG
import promobusque.ramon.promobusqueapp.ui.PromocoesFavoritasRecyclerAdapter
import com.google.firebase.database.GenericTypeIndicator




class PromocaoFavoritaFragment : Fragment() {

    private var contexto: Context? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private lateinit var promocoesFavoritas: MutableList<PromocaoFavorita>
    private var mChildEventListener: ChildEventListener? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: PromocoesFavoritasRecyclerAdapter
    private lateinit var mPromocoesFavoritasDatabaseReference: DatabaseReference
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, view: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_promocao_favorita, view, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        promocoesFavoritas = mutableListOf()

        recyclerView = view.findViewById(R.id.recycler_view_favorito)!!

        //Recycler View
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = linearLayoutManager

        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()

        mPromocoesFavoritasDatabaseReference = mFirebaseDatabase!!.reference.child("promocoesfavoritas")

        //attachDatabaseReadListener()

        setAdapter()
    }

    private fun setAdapter(){

        val user = obterIdUsuarioFirebase()
        val reference = mPromocoesFavoritasDatabaseReference.orderByKey()

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(erroDatabase: DatabaseError) {
                val message = erroDatabase.message
                Log.d(TAG,"Erro no firebase db: $message ")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG,"Snapshot favorita: $snapshot")
                var myUserId = obterIdUsuarioFirebase()

                val genericPromocaoFavorita = object : GenericTypeIndicator<PromocaoFavorita>() {
                }

                for (promocao in snapshot.children)
                {
                    var promocaoFavorita = promocao.getValue(genericPromocaoFavorita)

                    if(promocaoFavorita != null)
                    {
                        if(promocaoFavorita?.idUsuarioFirebase == myUserId)
                            promocoesFavoritas.add(promocaoFavorita)

                    }
                }

                adapter = PromocoesFavoritasRecyclerAdapter(promocoesFavoritas)
                adapter.notifyDataSetChanged()
                recyclerView?.adapter = adapter

            }

        })
    }




    fun attachDatabaseReadListener()
    {
        val myUserId = obterIdUsuarioFirebase()
        if(mChildEventListener == null) {
            mChildEventListener = object: ChildEventListener{
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FirebaseDatabase", "Erro no RealtimeFirebase: " + databaseError.message)
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            }

            mPromocoesFavoritasDatabaseReference.addChildEventListener(mChildEventListener!!)
        }
    }

    private fun obterIdUsuarioFirebase() : String {
        return mFirebaseAuth?.currentUser?.uid!!
    }

    override fun onAttach(context: Context) {
        contexto = context
        super.onAttach(context)
    }

    override fun onDetach() {
        if (mChildEventListener != null) {
            mPromocoesFavoritasDatabaseReference.removeEventListener(mChildEventListener!!)
            mChildEventListener = null
        }
        super.onDetach()
    }

}


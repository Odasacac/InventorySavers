package com.ifp.inventory_savers.adaptador;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifp.inventory_savers.ActualizarAlmacenFragment;
import com.ifp.inventory_savers.R;
import com.ifp.inventory_savers.modelo.Almacen;

import java.io.FileWriter;
import java.util.List;

public class AlmacenAdapter extends RecyclerView.Adapter<AlmacenAdapter.UserViewHolder>{

    private List<DocumentSnapshot> almacenes;
    private FirebaseFirestore mFirestore;
    private Activity activity;
    private final FragmentManager fm;

    public AlmacenAdapter(List<DocumentSnapshot> almacenes, FragmentManager supportFragmentManager) {
        this.almacenes = almacenes;
        this.fm=supportFragmentManager;
    }

    @NonNull
    @Override
    public AlmacenAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_almacen, parent, false);
        return new AlmacenAdapter.UserViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AlmacenAdapter.UserViewHolder holder, int position) {
        DocumentSnapshot documentSnapshot = almacenes.get(position); // Accede a los datos directamente desde la lista
        String documentoId = documentSnapshot.getId(); // Obtener el ID del documento para poder editar o boorrar
        Almacen almacen = documentSnapshot.toObject(Almacen.class); // Convertir instantánea a objeto Usuario ?¿?¿?¿?¿?¿?
        /**
         *  --------------- MODIFICADO ---------------
         */
        String nombreAlmacen = documentSnapshot.getString("nombreAlmacen"); // Obtener el nombre del almacén
        holder.nombreTextView.setText(nombreAlmacen);
        /**
         * ................ ^^^^^^^^^^^^ _____________
         */
        holder.botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirestore=FirebaseFirestore.getInstance();
                mFirestore.collection("Almacenes").document(documentoId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot BORRADO!");
                                // Aquí quitamos el almacen borrado de la vista para que desaparezca al borralo
                                almacenes.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, almacenes.size());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error al borrar...", e);
                            }
                        });
            }
        });
        holder.botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   /* Intent intent = new Intent(activity, CrearUsuarios.class);
                    intent.putExtra("id_usuario", documentoId);*/

                ActualizarAlmacenFragment actualizarAlmacenFragment = new ActualizarAlmacenFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id_almacen", documentoId);
                actualizarAlmacenFragment.setArguments(bundle);
                if (fm != null) {
                    actualizarAlmacenFragment.show(fm, "open fragment");
                } else {
                    Log.d(TAG, "Fragment manager es nulo!");
                }


            }
        });
    }
    @Override
    public int getItemCount() {
        return almacenes.size();
    }
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        ImageButton botonBorrar,botonEditar;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView=itemView.findViewById(R.id.textViewnombreAlmacen_itemAlmacen);

            botonBorrar=itemView.findViewById(R.id.imageButtonEliminar_ItemAlmacen);
            botonEditar=itemView.findViewById(R.id.imageButtonEditar_ItemAlmacen);
        }
    }


}

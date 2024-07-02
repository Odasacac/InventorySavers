package com.ifp.inventory_savers.adaptador;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.ifp.inventory_savers.ActualizarUsuarioFragment;
import com.ifp.inventory_savers.CrearUsuarios;
import com.ifp.inventory_savers.ListaUsuarios;
import com.ifp.inventory_savers.R;
import com.ifp.inventory_savers.modelo.Usuario;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UserViewHolder> {
    private List<DocumentSnapshot> usuarios;
    private FirebaseFirestore mFirestore;
    private Activity activity;
    private final FragmentManager fm;
    public UsuarioAdapter(List<DocumentSnapshot> usuarios, FragmentManager supportFragmentManager) {
        this.usuarios = usuarios;
        this.fm=supportFragmentManager;
    }
    @NonNull
    @Override
    public UsuarioAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UserViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        DocumentSnapshot documentSnapshot = usuarios.get(position); // Accede a los datos directamente desde la lista
        String documentoId = documentSnapshot.getId(); // Obtener el ID del documento para poder editar o boorrar
        Usuario usuario = documentSnapshot.toObject(Usuario.class); // Convertir instantánea a objeto Usuario
        holder.nombreTextView.setText(usuario.getNombre());
        holder.nivelPermisosTextView.setText(usuario.getNivelPermisos());
        holder.botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirestore=FirebaseFirestore.getInstance();
                mFirestore.collection("UserDependientes").document(documentoId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot BORRADO!");
                                // Aquí quitamos el susario borrado de la vista para que desaparezca al borralo
                                usuarios.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, usuarios.size());
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

                ActualizarUsuarioFragment actualizarUsuarioFragment=new ActualizarUsuarioFragment();
                Bundle bundle=new Bundle();
                bundle.putString("id_usuario", documentoId);
                actualizarUsuarioFragment.setArguments(bundle);
                if(fm!=null) {
                    actualizarUsuarioFragment.show(fm, "open fragment");
                }else{
                    Log.d(TAG, "Fragment manager es nulo!");
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return usuarios.size();
    }
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, nivelPermisosTextView;
        ImageButton botonBorrar,botonEditar;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView=itemView.findViewById(R.id.textViewnombreUsuario_itemUsuario);
            nivelPermisosTextView=itemView.findViewById(R.id.textViewNivelPermisos_itemUsuario);
            botonBorrar=itemView.findViewById(R.id.imageButtonEliminar_ItemUsuario);
            botonEditar=itemView.findViewById(R.id.imageButtonEditar_ItemUsuario);
        }
    }
}
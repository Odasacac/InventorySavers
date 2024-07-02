package com.ifp.inventory_savers.adaptador;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ifp.inventory_savers.R;
import com.ifp.inventory_savers.modelo.Producto;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {
    private final List<Producto> listaProductos;
    protected String nivelPermisos;
    private OnItemClickListener listener;
    private CheckBox chkMostrarSinStock;
    public ProductoAdapter(List<Producto> listaProductos, String nivelPermisos,CheckBox chkMostrarSinStock) {
        this.listaProductos = listaProductos;
        this.nivelPermisos = nivelPermisos;
        this.chkMostrarSinStock = chkMostrarSinStock;
    }
    public ProductoAdapter(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }
    // Interfaz para manejar los clics en los elementos ( la creo manualmente porque
    // el import lo hace a AdapterView, que no nos vale para esto.
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);
        holder.nombreTextView.setText(producto.getNombre());
        holder.descripcionTextView.setText(producto.getDescripcion());
        holder.precioTextView.setText(String.valueOf(producto.getPrecioCosto()));
        holder.cantidadTextView.setText(String.valueOf(producto.getCantidad()));
        holder.fechaExpiracionTextView.setText(producto.getFechaExpiracion());
        // Configura el OnClickListener para el elemento
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
        // Set up the delete button ( aqui no lo necesito)
        holder.btnEliminar.setVisibility(View.GONE);

        if (producto.getCantidad() == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FF8080"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#80FF80"));
        }

        if (producto.getCantidad() == 0 && !chkMostrarSinStock.isChecked()) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            param.height = 0;
            holder.itemView.setLayoutParams(param);
        } else {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            param.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.itemView.setLayoutParams(param);
        }
    }
    // Método para establecer el listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private void mostrarDialogoConfirmacion(Context context, String correoUsuarioOrg, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar Producto")
                .setMessage("¿Estás seguro de querer eliminar este producto?")
                .setPositiveButton("Sí", (dialogInterface, i) -> eliminarProducto(correoUsuarioOrg, position, context))
                .setNegativeButton("No", null)
                .show();
    }
    private void eliminarProducto(String correoUsuarioOrg, int position, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Productos").document(correoUsuarioOrg)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listaProductos.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, listaProductos.size());
                    Toast.makeText(context, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar el producto", Toast.LENGTH_SHORT).show());
    }
    @Override
    public int getItemCount() {
        return listaProductos.size();
    }
    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, descripcionTextView, precioTextView, cantidadTextView, fechaExpiracionTextView;
        Button btnEditar, btnEliminar;
        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.textViewNombreProducto);
            descripcionTextView = itemView.findViewById(R.id.textViewDescripcionProducto);
            precioTextView = itemView.findViewById(R.id.textViewPrecioProducto);
            cantidadTextView = itemView.findViewById(R.id.textViewCantidadProducto);
            fechaExpiracionTextView = itemView.findViewById(R.id.textViewFechaExpiracionProducto);
            btnEliminar = itemView.findViewById(R.id.buttonEliminarProducto);
        }
    }
}
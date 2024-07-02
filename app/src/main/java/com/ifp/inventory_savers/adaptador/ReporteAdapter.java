package com.ifp.inventory_savers.adaptador;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ifp.inventory_savers.R;
import com.ifp.inventory_savers.modelo.Producto;
import com.ifp.inventory_savers.modelo.Reporte;

import java.util.List;
public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder> {

    private List<Reporte> reportes;

    public ReporteAdapter(List<Reporte> reportes) {
        this.reportes = reportes;
        Log.d("ReporteAdapter", "Tamaño de la lista reportes: " + reportes.size());
    }

    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reportes, parent, false);
        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteViewHolder holder, int position) {
        Reporte reporte = reportes.get(position);
        holder.nombreAlmacenTextView.setText(reporte.getAlmacenes().get(0).getNombre()); // Using holder.nombreAlmacenTextView
        holder.itemView.setBackgroundColor(Color.parseColor("#B0C4DE"));
        if (reporte.getAlmacenes() != null && !reporte.getAlmacenes().isEmpty()) {

            Log.d("ReporteAdapter", "Nombre del almacén: " + reporte.getAlmacenes().get(0).getNombre());

            List<Producto> productos = reporte.getAlmacenes().get(0).getProductos(); // Suponiendo que existe el getter de productos
            Log.d("ReporteAdapter", "Cantidad de productos: " + productos.size());
            ProductoAdapter productoAdapter = new ProductoAdapter(productos);
            holder.productosRecyclerView.setAdapter(productoAdapter);

        }
    }

    @Override
    public int getItemCount() {
        return reportes.size();
    }

    static class ReporteViewHolder extends RecyclerView.ViewHolder {

        RecyclerView productosRecyclerView;
        TextView nombreAlmacenTextView;

        public ReporteViewHolder(@NonNull View itemView) {
            super(itemView);
            // ... existing code
            productosRecyclerView = itemView.findViewById(R.id.productosRecyclerView);
        }
    }
}


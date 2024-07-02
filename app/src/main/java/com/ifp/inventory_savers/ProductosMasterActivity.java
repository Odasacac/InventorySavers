package com.ifp.inventory_savers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifp.inventory_savers.adaptador.ProductosMasterAdapter;
import com.ifp.inventory_savers.modelo.Producto;

import java.util.ArrayList;
import java.util.List;

public class ProductosMasterActivity extends AppCompatActivity {
    private RecyclerView recyclerViewProductos;
    private ProductosMasterAdapter productoAdapter;
    private FirebaseFirestore db;
    private List<Producto> productos;
    private Button btnVolver, btnOrdenar, btnCrearProducto;
    private String nombreUsuario="";
    private String nivelPermisos="";

    private String correoUsuario="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_master);

        recyclerViewProductos = findViewById(R.id.recyclerView_productosMaster);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        productos = new ArrayList<>();

        btnCrearProducto = findViewById(R.id.btnCrearProducto_productosmaster);
        btnVolver = findViewById(R.id.btnVolver_productosMaster);

        nombreUsuario = getIntent().getStringExtra("userName");
        nivelPermisos = getIntent().getStringExtra("nivelPermisos");

        btnVolver.setOnClickListener(view -> {
            startActivity(new Intent(ProductosMasterActivity.this, MenuActivity.class));
            finish();
        });

        btnOrdenar = findViewById(R.id.btnOrdenar_productosMaster);

        btnCrearProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductosMasterActivity.this, CrearProductoActivity.class));
            }
        });

        //btnOrdenar.setOnClickListener(view -> mostrarOpcionesDeOrdenacion());

        cargarProductos();
    }

    private void cargarProductos() {
        correoUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Toast.makeText(this, "El usuario es: " + correoUsuario, Toast.LENGTH_SHORT).show();
        db.collection("Productos").whereEqualTo("correoUsuarioOrg",correoUsuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productos.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Producto producto = snapshot.toObject(Producto.class);
                        if (producto != null) {
                            producto.setIdProducto(snapshot.getId());
                            productos.add(producto);
                        }
                    }
                    if (productoAdapter == null) {
                        productoAdapter = new ProductosMasterAdapter(productos);
                        productoAdapter.setOnItemClickListener(position -> {
                            Producto productoActual = productos.get(position);
                            Intent intent = new Intent(ProductosMasterActivity.this, EditarProductoActivity.class);
                            // Asegúrate de que productoActual.getId() no devuelve null.
                            intent.putExtra("productoId", productoActual.getIdProducto());
                            startActivity(intent);
                        });
                        recyclerViewProductos.setAdapter(productoAdapter);
                    } else {
                        productoAdapter.notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar productos", Toast.LENGTH_SHORT).show());
    }

    //metodo para que el producto se actualice automaticamente
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Actualizar la lista de productos
            cargarProductos();
        }
    }
/*
    private void mostrarOpcionesDeOrdenacion() {
        String[] opciones = {"Orden alfabético", "Fecha de vencimiento más cercana", "Fecha de vencimiento más lejana"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ordenar por:");
        builder.setItems(opciones, (dialog, which) -> {
            switch (which) {
                case 0:
                    ordenarAlfabeticamente();
                    break;
                case 1:
                    ordenarPorFecha(true);
                    break;
                case 2:
                    ordenarPorFecha(false);
                    break;
            }
        });
        builder.show();
    }

    private void ordenarAlfabeticamente() {
        Collections.sort(productos, (p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()));
        productoAdapter.notifyDataSetChanged();
    }

    private void ordenarPorFecha(boolean masCercana) {
        Collections.sort(productos, (p1, p2) -> {
            Date fecha1 = p1.getFechaExpiracionDate();
            Date fecha2 = p2.getFechaExpiracionDate();
            return masCercana ? fecha1.compareTo(fecha2) : fecha2.compareTo(fecha1);
        });
        productoAdapter.notifyDataSetChanged();
    }

 */
}
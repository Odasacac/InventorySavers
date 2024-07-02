package com.ifp.inventory_savers;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifp.inventory_savers.modelo.Producto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditarProductoActivity extends AppCompatActivity {
    private EditText editTextNombre, editTextDescripcion, editTextPrecio, editTextCantidad, editTextFechaExpiracion,
            editTextProveedor, editTextCodigoBarras, editTextNivelReordenamiento;
    private TextView textViewFechaAgregado;
    private Spinner spinnerAlmacen;
    private FirebaseFirestore db;
    private String productoId;
    private ArrayAdapter<String> almacenAdapter;
    private List<String> almacenNombres = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);

        initializeFields();
        db = FirebaseFirestore.getInstance();
        loadAlmacenes();
        productoId = getIntent().getStringExtra("productoId");
        cargarDatosProducto();

        Button btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(view -> actualizarProducto());
    }

    private void initializeFields() {
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextPrecio = findViewById(R.id.editTextPrecio);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        editTextFechaExpiracion = findViewById(R.id.editTextFechaExpiracion);
        editTextProveedor = findViewById(R.id.editTextProveedor);
        editTextCodigoBarras = findViewById(R.id.editTextCodigoBarras);
        editTextNivelReordenamiento = findViewById(R.id.editTextNivelReordenamiento);
        textViewFechaAgregado = findViewById(R.id.textViewFechaAgregado);
        spinnerAlmacen = findViewById(R.id.spinnerAlmacen);
    }

    private void loadAlmacenes() {
        db.collection("Almacenes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        String almacenName = snapshot.getString("nombreAlmacen");
                        if (almacenName != null) {
                            almacenNombres.add(almacenName);
                        }
                    }
                    almacenAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, almacenNombres);
                    spinnerAlmacen.setAdapter(almacenAdapter);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar almacenes", Toast.LENGTH_SHORT).show());
    }

    private void cargarDatosProducto() {
        db.collection("Productos").document(productoId).get().addOnSuccessListener(documentSnapshot -> {
            Producto producto = documentSnapshot.toObject(Producto.class);
            if (producto != null) {
                editTextNombre.setText(producto.getNombre());
                editTextDescripcion.setText(producto.getDescripcion());
                editTextPrecio.setText(String.valueOf(producto.getPrecioCosto()));
                editTextCantidad.setText(String.valueOf(producto.getCantidad()));
                editTextFechaExpiracion.setText(producto.getFechaExpiracion());
                editTextProveedor.setText(producto.getProveedor());
                editTextCodigoBarras.setText(producto.getCodigoBarras());
                editTextNivelReordenamiento.setText(String.valueOf(producto.getNivelReordenamiento()));
                if (producto.getFechaAgregado() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    textViewFechaAgregado.setText("Fecha agregado: " + sdf.format(producto.getFechaAgregado()));
                } else {
                    textViewFechaAgregado.setText("Fecha agregado: No disponible");
                }
                // Set selected almacen
                if (almacenNombres.contains(producto.getAlmacen())) {
                    spinnerAlmacen.setSelection(almacenNombres.indexOf(producto.getAlmacen()));
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar los datos del producto", Toast.LENGTH_SHORT).show();
        });
    }

    private void actualizarProducto() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String correoUsuarioOrg= currentUser.getEmail();
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        double precio = Double.parseDouble(editTextPrecio.getText().toString().trim());
        int cantidad = Integer.parseInt(editTextCantidad.getText().toString().trim());
        String fechaExpiracion = editTextFechaExpiracion.getText().toString().trim();
        String proveedor = editTextProveedor.getText().toString().trim();
        String codigoBarras = editTextCodigoBarras.getText().toString().trim();
        int nivelReordenamiento = Integer.parseInt(editTextNivelReordenamiento.getText().toString().trim());
        String almacen = (String) spinnerAlmacen.getSelectedItem();

        Date oldFechaAgregado = new Date(); // You need to retrieve this correctly

        Producto producto = new Producto(correoUsuarioOrg,nombre, descripcion, precio, cantidad, fechaExpiracion, proveedor, codigoBarras, nivelReordenamiento, almacen, oldFechaAgregado);
        db.collection("Productos").document(productoId)
                .set(producto)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Producto actualizado con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar el producto", Toast.LENGTH_SHORT).show();
                });
    }
}
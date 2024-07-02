package com.ifp.inventory_savers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ifp.inventory_savers.modelo.Producto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CrearProductoActivity extends AppCompatActivity {
    private EditText editTextNombre, editTextDescripcion, editTextPrecio, editTextCantidad, editTextFechaExpiracion;
    private EditText editTextProveedor, editTextCodigoBarras, editTextNivelReordenamiento;
    private Spinner spinnerAlmacen;
    private FirebaseFirestore db;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private ArrayAdapter<String> almacenAdapter;
    private List<String> almacenNombres = new ArrayList<>();
    private ImageButton botonScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_producto);

        initializeFields();
        db = FirebaseFirestore.getInstance();
        loadAlmacenes();

        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnVolver = findViewById(R.id.btnVolver_productosMaster);
        btnGuardar.setOnClickListener(view -> guardarProducto());
        btnVolver.setOnClickListener(view -> finish());
        /**
         *  --------------- AÑADIDO ---------------
         */
        botonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creo una nueva instancia de IntentIntegrator, que es la clase principal de ZXing que maneja
                // el proceso de escaneo.
                IntentIntegrator integrator = new IntentIntegrator(CrearProductoActivity.this);
                // Establezco los formatos de códigos de barras que quiro que ZXing pueda
                // escanear ( al no saber que asticulos tendre que escanerar, los elijo tods).
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                // Establezco el mensaje que se mostrará en la pantalla de escaneo.
                integrator.setPrompt("Escanea el código de barras");
                // Establezco el ID de la cámara que quiero usar para el escaneo. El valor 0 generalmente se refiere a la cámara trasera.
                integrator.setCameraId(0);
                // Desactivo el sonido de "beep" que se reproduce cuando se escanea un código de barras.
                integrator.setBeepEnabled(true);
                // Desactivo la opción de guardar la imagen del código de barras escaneado.
                integrator.setBarcodeImageEnabled(false);
                // Bloqueo la orientación de la actividad de escaneo en la orientación actual del dispositivo ( sino se
                // pone en horizontal por defecto y los Toasts se ven mal).
                integrator.setOrientationLocked(true);
                // Inicio el proceso de escaneo.
                integrator.initiateScan();
            }
        });
    }
    /**
     * ................ ^^^^^^^^^^^^ _____________
     */
    private void initializeFields() {
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextPrecio = findViewById(R.id.editTextPrecio);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        editTextFechaExpiracion = findViewById(R.id.editTextFechaExpiracion);
        editTextProveedor = findViewById(R.id.editTextProveedor);
        editTextCodigoBarras = findViewById(R.id.editTextCodigoBarras);
        editTextNivelReordenamiento = findViewById(R.id.editTextNivelReordenamiento);
        spinnerAlmacen = findViewById(R.id.spinnerAlmacen);
        botonScan=findViewById(R.id.imageButtonCapturaCodigo_crearProducto);
    }

    private void loadAlmacenes() {
        String correoUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("Almacenes") .whereEqualTo("correoUsuarioOrg",correoUsuario)
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

    private void guardarProducto() {


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String correoUsuarioOrg= currentUser.getEmail();
        String nombre = editTextNombre.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String almacen = (String) spinnerAlmacen.getSelectedItem();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El campo nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }
        if (descripcion.isEmpty()) {
            Toast.makeText(this, "El campo descripción es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }
        if (almacen == null || almacen.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un almacén", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = Double.parseDouble(editTextPrecio.getText().toString().trim());
        int cantidad = Integer.parseInt(editTextCantidad.getText().toString().trim());
        String fechaExpiracion = editTextFechaExpiracion.getText().toString().trim();
        try {
            sdf.setLenient(false);
            sdf.parse(fechaExpiracion);
        } catch (ParseException e) {
            Toast.makeText(this, "Fecha de expiración no válida. Formato correcto: dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }
        String proveedor = editTextProveedor.getText().toString().trim();
        String codigoBarras = editTextCodigoBarras.getText().toString().trim();
        int nivelReordenamiento = Integer.parseInt(editTextNivelReordenamiento.getText().toString().trim());
        Date fechaAgregado = new Date();

        Producto producto = new Producto(correoUsuarioOrg,nombre, descripcion, precio, cantidad, fechaExpiracion, proveedor, codigoBarras, nivelReordenamiento, almacen, fechaAgregado);
        db.collection("Productos").add(producto)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Producto guardado con éxito", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CrearProductoActivity.this, ProductosMasterActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar el producto", Toast.LENGTH_SHORT).show());
        /*
        subirImagenYObtenerURL(imagenUri, new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String imagenUrl) {
                Producto producto = new Producto(correoUsuarioOrg,nombre, descripcion, precio, cantidad, fechaExpiracion, proveedor, codigoBarras, nivelReordenamiento, almacen, fechaAgregado, imagenUrl);
                db.collection("Productos").add(producto)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(CrearProductoActivity.this, "Producto guardado con éxito", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(CrearProductoActivity.this, "Error al guardar el producto", Toast.LENGTH_SHORT).show());
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CrearProductoActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
            }
        });

         */
    }
    /**
     *  --------------- AÑADIDO ---------------
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Parseo el resultado del escaneo.
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // Compruebo si el resultado del escaneo es nulo.
        if(result != null) {
            // Si el contenido del resultado es nulo, significa que el escaneo fue cancelado.
            if(result.getContents() == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();
            } else {
                // Si el contenido del resultado no es nulo, significa que se escaneó un código de barras. Muestro el contenido del código de barras en un Toast y lo pongo en el campo editTextCodigoBarras.
                editTextCodigoBarras.setText(result.getContents());
                Toast.makeText(this, "Código escaneado: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // Si el resultado es nulo, llamo al método onActivityResult de la superclase.
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    /**
     * ................ ^^^^^^^^^^^^ _____________
     */
    /*
    private void subirImagenYObtenerURL(Uri imagenUri, OnSuccessListener<String> onSuccess, OnFailureListener onFailure) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagenRef = storageRef.child("imagenes/" + imagenUri.getLastPathSegment());
        UploadTask uploadTask = imagenRef.putFile(imagenUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imagenUrl = uri.toString();
                onSuccess.onSuccess(imagenUrl);
            }).addOnFailureListener(onFailure);
        }).addOnFailureListener(onFailure);
    }
    */

}
package com.ifp.inventory_savers;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ifp.inventory_savers.R;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifp.inventory_savers.adaptador.ProductoAdapter;
import com.ifp.inventory_savers.modelo.Producto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProductosActivity extends AppCompatActivity {
    private RecyclerView recyclerViewProductos;
    private ProductoAdapter productoAdapter;
    private FirebaseFirestore db;
    private List<Producto> productos;
    private Button btnVolver, btnOrdenar;
    private String nombreUsuario="";
    private String nivelPermisos="";
    private String correoUsuario="";
    private String nombreAlmacenSeleccionado="";
    private CheckBox chkMostrarSinStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        recyclerViewProductos = findViewById(R.id.recyclerView_productosMaster);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        productos = new ArrayList<>();

        btnVolver = findViewById(R.id.btnVolver_productosMaster);

        nombreAlmacenSeleccionado=getIntent().getStringExtra("nombreAlmacen");
        nombreUsuario = getIntent().getStringExtra("userName");
        nivelPermisos= getIntent().getStringExtra("nivelPermisos");

        chkMostrarSinStock = findViewById(R.id.chkMostrarSinStock_productos);
        chkMostrarSinStock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            productoAdapter.notifyDataSetChanged();
        });
        // Visibilidad del checkbox según permisos
        if(nivelPermisos.equals("Empleado")){
            Log.e(TAG,"Adaptador nivel permisos en if= "+nivelPermisos);
            chkMostrarSinStock.setVisibility(View.GONE);
        }else{
            chkMostrarSinStock.setVisibility(View.VISIBLE);
            Log.e(TAG,"Adaptador nivel permisos en else= "+nivelPermisos);
        }
        btnVolver.setOnClickListener(view -> {
           startActivity(new Intent(ProductosActivity.this,SeleccionAlmacenesUsuarioDep.class));
           finish();
        });


        btnOrdenar = findViewById(R.id.btnOrdenar_productosMaster);
        btnOrdenar.setOnClickListener(view -> mostrarOpcionesDeOrdenacion());

        cargarProductos();

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Verifica si hay un PIN guardado
        SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        String pinGuardado = sharedPreferences.getString("PIN", null);
        Log.d(TAG, "PIN recuperado: " + pinGuardado);
        if (pinGuardado != null) {
            // PIN encontrado, solicita al usuario que lo ingrese
            mostrarDialogoPin(pinGuardado);
        }
    }
    private void cargarProductos() {
        correoUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d(TAG, "El usuario es: " + correoUsuario);
        //Toast.makeText(this, "El usuario es: " + correoUsuario, Toast.LENGTH_SHORT).show();
        db.collection("Productos")
                .whereEqualTo("correoUsuarioOrg",correoUsuario)
                .whereEqualTo("almacen", nombreAlmacenSeleccionado)
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
                        productoAdapter = new ProductoAdapter(productos,nivelPermisos,chkMostrarSinStock);
                        recyclerViewProductos.setAdapter(productoAdapter);
                        productoAdapter.setOnItemClickListener(new ProductoAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                mostrarDialogoModificarCantidad(position);
                            }
                        });
                    } else {
                        productoAdapter.notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar productos", Toast.LENGTH_SHORT).show());
    }


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
    private void mostrarDialogoPin(final String pinGuardado) {
        // Guardo el booleno para qeu la primera vez que se conecte no salte el dialog del PIN
        Log.d(TAG, "ME han llamado mostrarDialogoPin:PIN recuperado: " + pinGuardado);
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        boolean primeraVez = prefs.getBoolean("primeraVez", true);
        SharedPreferences.Editor editor = prefs.edit();
        // Si es la primera vez, actualiza la preferencia
        if (primeraVez) {
            editor.putBoolean("primeraVez", false);
            editor.apply();
            return; // Este return termina con la ejjecución del método ya que se cumple el booleano.
        }
        // Si primeraVez es falso aparece el dialog para que se ingrese el PIN de nuevo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese PIN");
        // Crea un EditText para ingresar el PIN
        final EditText editTextPin = new EditText(this);
        editTextPin.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(editTextPin);
        // Aqui declaro TEMPORALMENTE el comportamiento del botón como nulo ( sino el dialog se cerraría
        // aunque el PIN fuese incorrecto)
        builder.setPositiveButton("Aceptar", null);
        builder.setNegativeButton("Cerrar Sesión",null);
        AlertDialog dialog = builder.create();
        dialog.show();
        // Aqui creo un listener espeícfico para cada boton ( por lo que comento antes del comportamiento
        // por defecto del boton en el dialog...)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinIngresado = editTextPin.getText().toString();
                if (pinIngresado.equals(pinGuardado)) {
                    // PIN correcto, continuar con la aplicación
                    dialog.dismiss();
                } else {
                    Toast.makeText(ProductosActivity.this, "PIN incorrecto", Toast.LENGTH_SHORT).show();
                }
            }
        }); dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductosActivity.this,SeleccionAlmacenesUsuarioDep.class));
            }
        });


    }
    public void mostrarDialogoModificarCantidad(int position) {
        Producto producto = productos.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Creo un TextView para el título ya que por defecto el titlo no se puede cambiar de posición
        // en un dialog.
        TextView title = new TextView(this);
        title.setText("Modificar cantidad");
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
        title.setTextColor(Color.BLACK);
        builder.setCustomTitle(title);  // Uso setCustomTitle() en lugar de setTitle() por lo comentado antes

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);

        Button botonMenos = new Button(this);
        botonMenos.setText("-");
        layout.addView(botonMenos);

        EditText editTextCantidad = new EditText(this);
        editTextCantidad.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextCantidad.setText(String.valueOf(producto.getCantidad()));
        layout.addView(editTextCantidad);

        Button botonMas = new Button(this);
        botonMas.setText("+");
        layout.addView(botonMas);

        builder.setView(layout);

        botonMenos.setOnClickListener(v -> {
            int cantidad = Integer.parseInt(editTextCantidad.getText().toString());
            if (cantidad > 0) {
                cantidad--;
                editTextCantidad.setText(String.valueOf(cantidad));
            }
        });

        botonMas.setOnClickListener(v -> {
            int cantidad = Integer.parseInt(editTextCantidad.getText().toString());
            cantidad++;
            editTextCantidad.setText(String.valueOf(cantidad));
        });

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            int cantidad = Integer.parseInt(editTextCantidad.getText().toString());
            producto.setCantidad(cantidad);
            productoAdapter.notifyDataSetChanged();
            // Actualiza la cantidad en la base de datos
            actualizarCantidadEnBaseDeDatos(producto);

        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }
    public void actualizarCantidadEnBaseDeDatos(Producto producto) {
        db = FirebaseFirestore.getInstance();
        DocumentReference productoRef = db.collection("Productos").document(producto.getIdProducto());

        productoRef.update("cantidad", producto.getCantidad())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cantidad actualizada correctamente");
                    Toast.makeText(this, "Cantidad actualizada correctamente.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error al actualizar la cantidad", e);
                    Toast.makeText(this, "Error al actualizar la cantidad.", Toast.LENGTH_SHORT).show();

                });
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dependiente_menu, menu);
        return true;
    }
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item_au_dep:
                String url = "https://sites.google.com/view/proyecto-2dam-grupo17/inicio";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            case R.id. item_rrss_dep:
                Toast.makeText(this, "Dirigiendo a redes sociales", Toast.LENGTH_SHORT).show();
                return true;
            case R.id. item_logout_dep:
                startActivity(new Intent(ProductosActivity.this, SeleccionAlmacenesUsuarioDep.class));
                return true;

            case R.id. item_salir_dep:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    */

}
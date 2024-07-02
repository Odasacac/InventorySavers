package com.ifp.inventory_savers;

import static android.content.ContentValues.TAG;
import static android.os.Environment.getExternalStorageDirectory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ifp.inventory_savers.adaptador.ReporteAdapter;
import com.ifp.inventory_savers.modelo.Almacen;
import com.ifp.inventory_savers.modelo.Producto;
import com.ifp.inventory_savers.modelo.Reporte;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportesActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    protected TextView card_cantidadMinima,
            card_Resultado,card_caducados,card_qtyCaducados,card_perdidas,card_cantidadPerdidas;
    protected Button botonAceptar, botonVolver;

    private FirebaseFirestore db;
    private String correoUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);


        botonAceptar = findViewById(R.id.buttonAceptar_reportes);
        botonVolver = findViewById(R.id.buttonVolver_reportes);
        card_cantidadMinima = findViewById(R.id.card_cantidadMinima);
        card_Resultado = findViewById(R.id.card_Resultado);
        card_caducados = findViewById(R.id.card_caducados);
        card_qtyCaducados = findViewById(R.id.card_qtyCaducados);
        card_perdidas = findViewById(R.id.card_perdidas);
        card_cantidadPerdidas = findViewById(R.id.card_cantidadPerdidas);

        db = FirebaseFirestore.getInstance();

        obtenerProductosCaducados();
        obtenerProductosConCantidadCero();
        calcularPerdidas();

        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportesActivity.this, MenuActivity.class));
            }
        });
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Solicita permisos de almacenamiento
                verifyStoragePermissions(ReportesActivity.this);
                exportarDatosCSV();
            }
        });

    }
    // abrahamgranadossanchez@gmail.com
    private void exportarDatosCSV() {
        correoUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String nombreArchivo = "reportes.csv";
        File file = new File(getExternalFilesDir(null), nombreArchivo);
        // Verifica si el archivo existe
        if (file.exists()) {
            Log.d("Archivo", "El archivo existe: "+nombreArchivo);
            Log.e("pacage: ",this.getApplicationContext().getPackageName());
        } else {
            Log.d("Archivo", "El archivo no existe "+nombreArchivo);
            Log.e("pacage: ",this.getApplicationContext().getPackageName());
        }
        try {
            FileWriter out = new FileWriter(file);
            // Escribe los encabezados del CSV
            out.append("Tipo de Reporte");
            out.append(",");
            out.append("Cantidad");
            out.append("\n");

            out.append("Productos con cantidad = 0");
            out.append(",");
            out.append(card_Resultado.getText().toString());
            out.append("\n");

            out.append("Productos caducados");
            out.append(",");
            out.append(card_qtyCaducados.getText().toString());
            out.append("\n");

            out.append("Pérdidas totales");
            out.append(",");
            out.append(card_cantidadPerdidas.getText().toString());
            out.append("\n");
            // rellena con todos los datos del usuario
            out.append("Nombre");
            out.append(",");
            out.append("Descripción");
            out.append(",");
            out.append("Precio");
            out.append(",");
            out.append("Cantidad");
            out.append(",");
            out.append("Fecha de Expiración");
            out.append(",");
            out.append("Proveedor");
            out.append(",");
            out.append("Código de Barras");
            out.append(",");
            out.append("Nivel de Reordenamiento");
            out.append(",");
            out.append("Almacén");
            out.append(",");
            out.append("Fecha Agregado");
            out.append("\n");

            obtenerTodosLosProductos(file);

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (file.exists()) {
            Uri path = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("vnd.android.cursor.dir/email");
            String to[] = {correoUsuario};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.putExtra(Intent.EXTRA_STREAM, path);
            emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reporte");
            List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                this.grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivity(Intent.createChooser(emailIntent, "Enviar reporte..."));
        } else {
        }
    }
    private void obtenerTodosLosProductos(File file) {
        correoUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("Productos")
                .whereEqualTo("correoUsuarioOrg", correoUsuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                 Producto producto = document.toObject(Producto.class);
                                // Aquí es donde debes agregar los datos de tus productos
                                try {
                                    FileWriter out = new FileWriter(file, true); // Asegúrate de abrir el archivo en modo append
                                    out.append(producto.getNombre());
                                    out.append(",");
                                    out.append(producto.getDescripcion());
                                    out.append(",");
                                    out.append(String.valueOf(producto.getPrecioCosto()));
                                    out.append(",");
                                    out.append(String.valueOf(producto.getCantidad()));
                                    out.append(",");
                                    out.append(producto.getFechaExpiracion());
                                    out.append(",");
                                    out.append(producto.getProveedor());
                                    out.append(",");
                                    out.append(producto.getCodigoBarras());
                                    out.append(",");
                                    out.append(String.valueOf(producto.getNivelReordenamiento()));
                                    out.append(",");
                                    out.append(producto.getAlmacen());
                                    out.append(",");
                                    out.append(producto.getFechaAgregado().toString());
                                    out.append("\n");
                                    out.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error obteniendo productos: ", task.getException());
                        }
                    }
                });
    }

    private void obtenerProductosConCantidadCero() {
        correoUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("Productos")  .whereEqualTo("correoUsuarioOrg",correoUsuario)
                .whereEqualTo("cantidad", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int numProductos = task.getResult().size();
                            card_cantidadMinima.setText("Productos con cantidad = 0");
                            card_Resultado.setText(String.valueOf(numProductos));
                        } else {
                            Log.d(TAG, "Error obteniendo productos con cantidad = 0: ", task.getException());
                        }
                    }
                });
    }

    private void obtenerProductosCaducados() {
        // Obtén la fecha actual
        Date currentDate = new Date();
        correoUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("Productos")  .whereEqualTo("correoUsuarioOrg",correoUsuario)
                .whereLessThan("fechaExpiracionDate", currentDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int numProductos = task.getResult().size();
                            card_caducados.setText("Productos caducados");
                            card_qtyCaducados.setText(String.valueOf(numProductos));
                        } else {
                            Log.d(TAG, "Error obteniendo productos caducados: ", task.getException());
                        }
                    }
                });
    }

    private void calcularPerdidas() {
        // Obtén la fecha actual
        Date currentDate = new Date();
        correoUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("Productos")  .whereEqualTo("correoUsuarioOrg",correoUsuario)
                .whereLessThan("fechaExpiracionDate", currentDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double totalPerdidas = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                double precioCostoProducto = document.getDouble("precioCosto");
                                int cantidadProducto = document.getLong("cantidad").intValue();
                                totalPerdidas += precioCostoProducto * cantidadProducto;
                            }
                            card_perdidas.setText("Pérdidas totales");
                            card_cantidadPerdidas.setText(String.valueOf(totalPerdidas));
                        } else {
                            Log.d(TAG, "Error calculando pérdidas: ", task.getException());
                        }
                    }
                });
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Comprueba si tenemos permiso de escritura
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // No tenemos permiso, así que pedimos al usuario que lo habilite
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    @Override
        public boolean onCreateOptionsMenu (Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.general_menu, menu);
            return true;
        }
    }

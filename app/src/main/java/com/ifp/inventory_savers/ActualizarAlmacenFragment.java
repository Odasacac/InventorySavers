package com.ifp.inventory_savers;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActualizarAlmacenFragment extends DialogFragment {
    public String id_almacen;

    private TextView label1, label2;

    private EditText caja1;

    private Button boton1, boton2;

    private FirebaseFirestore mFirestore;

    private ArrayAdapter<String> adapterLista;

    private String nombreAlmacen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            id_almacen = getArguments().getString("id_almacen");
            obtenerDatosAlmacen(id_almacen);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_actualizar_almacen, container, false);
        label1 = (TextView) view.findViewById(R.id.textViewtitulo_fragmentCrearAlmacen);
        label2 = (TextView) view.findViewById(R.id.textViewNombre_fragmentCrearAlmacen);

        caja1 = (EditText) view.findViewById(R.id.caja1_FragmentActualizarAlmacen);
        boton1 = (Button) view.findViewById(R.id.buttonActualzar_fragmentCrearAlmacen);
        boton2 = (Button) view.findViewById(R.id.buttonVolver_fragmentCrearAlmacen);

        // Instancio base de datos cloud firestone
        mFirestore = FirebaseFirestore.getInstance();


        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombreAlmacen = caja1.getText().toString();
                if (nombreAlmacen.isEmpty()) {
                    Toast.makeText(getContext(), "Campo nombre obligatorio.", Toast.LENGTH_SHORT).show();
                } else {
                    updateAlmacen(nombreAlmacen, id_almacen);
                }
            }
        });
        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    private void obtenerDatosAlmacen(String idAlmacen) {
        // Aquí realizas la consulta a la base de datos para obtener los datos del almacen con el ID proporcionado
        // Por ejemplo, utilizando FirebaseFirestore
        FirebaseFirestore.getInstance().collection("Almacenes").document(idAlmacen)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Verifica si el documento existe y contiene datos
                        if (documentSnapshot.exists()) {
                            // Obtiene los datos del documento y los establece en los campos de texto del fragmento
                            String nombreAlmacen_actualizar = documentSnapshot.getString("nombreAlmacen");

                            // Establece los datos en los campos de texto
                            caja1.setText(nombreAlmacen_actualizar);

                        } else {
                            // Si el documento no existe o está vacío, muestra un mensaje de error
                            Log.d(TAG, "El documento no existe o está vacío");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Maneja el error si la consulta falla
                        Log.e(TAG, "Error al obtener los datos del usuario", e);
                    }
                });
    }

    private void updateAlmacen(String nombreAlmacen, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("nombreAlmacen", nombreAlmacen);
        mFirestore.collection("Almacenes").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Reiniciar la actividad ListaAlmacenes
                Intent intent = new Intent(getActivity(), ListaAlmacenes.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                // Toast.makeText(getContext(), "Usuario actualizado correctamente.", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Toast.makeText(getContext(), "Error al actualizar el usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

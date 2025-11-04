import java.io.*;
import java.util.*;

public class Comunicado implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String tipo;
    private HashMap<String, Object> dados;

    public Comunicado(String tipo)
    {
        this.tipo = tipo;
        this.dados = new HashMap<>();
    }

    public String getTipo()
    {
        return this.tipo;
    }

    public void setDado(String chave, Object valor)
    {
        this.dados.put(chave, valor);
    }

    public Object getDado(String chave)
    {
        return this.dados.get(chave);
    }

    public String getDadoString(String chave)
    {
        Object valor = this.dados.get(chave);
        return valor != null ? valor.toString() : null;
    }

    public HashMap<String, Object> getDados()
    {
        return this.dados;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"tipo\": \"").append(this.tipo).append("\",\n");
        sb.append("  \"dados\": {\n");

        int count = 0;
        for (Map.Entry<String, Object> entry : this.dados.entrySet())
        {
            if (count > 0) sb.append(",\n");
            sb.append("    \"").append(entry.getKey()).append("\": ");

            if (entry.getValue() instanceof String)
                sb.append("\"").append(entry.getValue()).append("\"");
            else
                sb.append(entry.getValue());

            count++;
        }

        sb.append("\n  }\n");
        sb.append("}");
        return sb.toString();
    }
}
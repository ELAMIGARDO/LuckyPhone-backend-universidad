package Luckyphone.dto;

import Luckyphone.entity.DetalleVenta;
import Luckyphone.entity.MetodoPago;

import java.util.List;

public class CrearVentaRequest {

    private MetodoPago metodoPago;
    private String tipoComprobante;
    private List<DetalleVenta> detalles;

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
}

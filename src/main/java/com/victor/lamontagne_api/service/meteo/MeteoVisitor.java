package com.victor.lamontagne_api.service.meteo;

public interface MeteoVisitor {
    void visitMeteoblue(MeteoBlueProvider provider);
    void visitMeteoFrance(MeteoFranceProvider provider);
}

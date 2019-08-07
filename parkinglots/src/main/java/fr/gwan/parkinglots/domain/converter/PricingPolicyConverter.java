package fr.gwan.parkinglots.domain.converter;

import org.springframework.stereotype.Component;
import fr.gwan.parkinglots.domain.PricingPolicy;

@Component
public class PricingPolicyConverter
    implements EntityConverter<fr.gwan.parkinglots.api.model.PricingPolicy, PricingPolicy> {


    @Override
    public PricingPolicy toEntity(fr.gwan.parkinglots.api.model.PricingPolicy pricingPolicyApi) {
    	PricingPolicy pricingPolicyEntity = new PricingPolicy();
    	pricingPolicyEntity.setSedanPricingPolicy(pricingPolicyApi.getSedanPricingPolicy());
    	pricingPolicyEntity.set20kwPricingPolicy(pricingPolicyApi.get20kwPricingPolicy());
    	pricingPolicyEntity.set50kwPricingPolicy(pricingPolicyApi.get50kwPricingPolicy());

        return pricingPolicyEntity;
    }

    @Override
    public fr.gwan.parkinglots.api.model.PricingPolicy toApi(PricingPolicy pricingPolicyEntity) {
    	fr.gwan.parkinglots.api.model.PricingPolicy pricingPolicyApi = new fr.gwan.parkinglots.api.model.PricingPolicy();
    	pricingPolicyApi.setSedanPricingPolicy(pricingPolicyEntity.getSedanPricingPolicy());
    	pricingPolicyApi.set20kwPricingPolicy(pricingPolicyEntity.get20kwPricingPolicy());
    	pricingPolicyApi.set50kwPricingPolicy(pricingPolicyEntity.get50kwPricingPolicy());
        return pricingPolicyApi;
    }

}
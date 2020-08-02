package com.fut.desktop.app.futservice.utils;

import com.fut.desktop.app.models.PrivilegeLevel;
import com.fut.desktop.app.models.RoleLevel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RolePrivilegeMapper {

    public List<GrantedAuthority> getPrivelges(RoleLevel role) {
        List<GrantedAuthority> authorityList = new ArrayList<>();

        if(role == RoleLevel.ROLE_BRONZE){
            authorityList.add(new SimpleGrantedAuthority(PrivilegeLevel.Normal.name()));
        }

        if(role == RoleLevel.ROLE_SILVER){
            authorityList.add(new SimpleGrantedAuthority(PrivilegeLevel.AutoBid.name()));
        }

        if(role == RoleLevel.ROLE_GOLD){
            authorityList.add(new SimpleGrantedAuthority(PrivilegeLevel.Analytics.name()));
        }

        return authorityList;
    }
}

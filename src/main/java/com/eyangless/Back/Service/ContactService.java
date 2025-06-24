package com.eyangless.Back.Service;

import com.eyangless.Back.DTO.ContactDTO;

import java.util.List;
import java.util.Map;

public interface ContactService {
    Map<String, Object> addContact(String citeid, List<ContactDTO> dtos, String autheader);
}

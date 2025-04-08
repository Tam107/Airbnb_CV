package org.example.backend.listing.application;

import lombok.RequiredArgsConstructor;
import org.example.backend.listing.application.dto.CreatedListingDTO;
import org.example.backend.listing.application.dto.DisplayCardListingDTO;
import org.example.backend.listing.application.dto.SaveListingDTO;
import org.example.backend.listing.domain.Listing;
import org.example.backend.listing.mapper.ListingMapper;
import org.example.backend.listing.repository.ListingRepository;
import org.example.backend.user.application.UserService;
import org.example.backend.user.application.dto.Auth0Service;
import org.example.backend.user.application.dto.ReadUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LandlordService {

    private final ListingRepository listingRepository;

    private final PictureService pictureService;

    private final ListingMapper listingMapper;

    private final UserService userService;

    private final Auth0Service auth0Service;

    @Autowired
    public LandlordService(ListingRepository listingRepository, ListingMapper listingMapper, UserService userService, Auth0Service auth0Service, PictureService pictureService) {
        this.listingRepository = listingRepository;
        this.listingMapper = listingMapper;
        this.userService = userService;
        this.auth0Service = auth0Service;
        this.pictureService = pictureService;
    }


    @Transactional(rollbackFor = Exception.class)
    public CreatedListingDTO create(SaveListingDTO saveListingDTO){
        Listing newListing = listingMapper.saveListingDTOToListing(saveListingDTO);

        ReadUserDTO userConnected = userService.getAuthenticatedUserFromSecurityContext();

        newListing.setLandlordPublicId(userConnected.publicId());

        Listing savedListing = listingRepository.saveAndFlush(newListing);

        pictureService.saveAll(saveListingDTO.getPictures(), savedListing);

        auth0Service.addLandLordRoleToUser(userConnected);
        return listingMapper.listingToCreatedListingDTO(savedListing);
    }

    @Transactional( readOnly = true)
    public List<DisplayCardListingDTO> getAllProperties(ReadUserDTO landlord){
        List<Listing> properties = listingRepository.findAllByLandlordPublicIdFetchCoverPicture(landlord.publicId());

        return listingMapper.listingToDisplayCardListingDTOs(properties);
    }



}

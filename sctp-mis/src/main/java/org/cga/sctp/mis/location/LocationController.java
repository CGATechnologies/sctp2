/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, CGATechnologies
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cga.sctp.mis.location;


import org.cga.sctp.location.*;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.mis.core.templating.SelectOptionItem;
import org.cga.sctp.terminology.TerminologyService;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.cga.sctp.mis.location.LocationCodeUtil.toSelectOptions;

/**
 * This is mostly an MVC controller but also contains some controller actions that
 * server AJAX requests.
 *
 */
@Controller
@RequestMapping("/locations")
public class LocationController extends BaseController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private TerminologyService terminologyService;

    private List<Location> getActiveParentLocationsForType(LocationType type) {
        return type.isRoot ? List.of() : locationService.getActiveByType(type.parent);
    }

    private String getLocationParentType(LocationType type) {
        return type.isRoot ? "Country" : terminologyService.getTerminologyByName(type.parent.name()).getDescription();
    }

    @GetMapping
    @AdminAndStandardAccessOnly
    ModelAndView getAllLocations(@RequestParam(value = "type", defaultValue = "COUNTRY", required = false) LocationType type) {

        return view("/locations/list")
                .addObject("type", type)
                .addObject("terminologies", terminologyService.getAllTerminologies())
                .addObject("locations", locationService.getByType(type));
    }

    @GetMapping("/{location-id}/sublocations")
    @AdminAndStandardAccessOnly
    ModelAndView getAllLocations(@PathVariable("location-id") Long locationId, RedirectAttributes attributes) {
        final Location location = locationService.findById(locationId);
        String returnUrl;
        if (location == null) {
            setDangerFlashMessage("Location does not exist", attributes);
            return redirect("/locations");
        }
        List<LocationInfo> locations = locationService.getByParent(location);
        if (location.getLocationType().isRoot) {
            returnUrl = "/locations";
        } else {
            returnUrl = format("/locations/%d/sublocations", location.getParentId());
        }
        return view("/locations/list")
                .addObject("parent", location)
                .addObject("locations", locations)
                .addObject("returnUrl", returnUrl)
                .addObject("terminologies", terminologyService.getAllTerminologies())
                .addObject("type", location.getLocationType());
    }

    @GetMapping("/new")
    @AdminAndStandardAccessOnly
    ModelAndView newLocation(@RequestParam("type") LocationType type, @ModelAttribute("locationForm") NewLocationForm locationForm) {
        locationForm.setType(type);
        return view("/locations/new")
                .addObject("parents", getActiveParentLocationsForType(type))
                .addObject("locationType", terminologyService.getTerminologyByName(type.name()))
                .addObject("parentType", getLocationParentType(type))
                .addObject("booleans", Booleans.VALUES);
    }

    @PostMapping
    @AdminAndStandardAccessOnly
    ModelAndView addLocation(
            @Validated @ModelAttribute("locationForm") NewLocationForm locationForm,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            return view("/locations/new")
                    .addObject("parents", getActiveParentLocationsForType(locationForm.getType()))
                    .addObject("booleans", Booleans.VALUES);
        }

        if (locationForm.getParent() != null) {
            if (!locationForm.getType().isRoot) {
                Location parentLocation = locationService.findActiveLocationById(locationForm.getParent());
                if (parentLocation == null) {
                    setDangerFlashMessage(format("Selected %s is not currently active.",
                            locationForm.getType().parent.description), attributes);
                    return view("/locations/new")
                            .addObject("parents", getActiveParentLocationsForType(locationForm.getType()))
                            .addObject("booleans", Booleans.VALUES);
                }
                if (!locationForm.getType().isImmediateChildOf(parentLocation.getLocationType())) {
                    setDangerFlashMessage(
                            format(
                                    "Invalid location selection. %s must be directly under %s.",
                                    locationForm.getType().description,
                                    locationForm.getType().parent.description
                            ),
                            attributes
                    );
                    return view("/locations/new")
                            .addObject("parents", getActiveParentLocationsForType(locationForm.getType()))
                            .addObject("booleans", Booleans.VALUES);
                }
            }
        } else {
            if (!locationForm.getType().isRoot) {
                result.addError(new FieldError("locationForm", "parent", format("%s is required.",
                        locationForm.getType().parent.description)));
                return view("/locations/new")
                        .addObject("parents", getActiveParentLocationsForType(locationForm.getType()))
                        .addObject("booleans", Booleans.VALUES);
            }
        }

        Location conflict = locationService.findByCode(locationForm.getCode());
        if (conflict != null) {
            ModelAndView view = view("/locations/new")
                    .addObject("parents", getActiveParentLocationsForType(locationForm.getType()))
                    .addObject("booleans", Booleans.VALUES);
            return setDangerMessage(view, format("Selected code is already assigned to %s.", conflict.getName()));
        }

        Location location = new Location();
        location.setLatitude(BigDecimal.ZERO);
        location.setLongitude(BigDecimal.ONE);
        location.setName(locationForm.getName());
        location.setCreatedAt(LocalDateTime.now());
        location.setParentId(locationForm.getParent());
        location.setLocationType(locationForm.getType());
        location.setActive(locationForm.getActive().value);
        location.setCode(locationForm.getCode());

        locationService.save(location);

        setSuccessFlashMessage("Location successfully added", attributes);

        if (location.getLocationType().isRoot) {
            return redirect("/locations");
        } else {
            return redirect(format("/locations/%d/sublocations", location.getParentId()));
        }
    }

    @PostMapping("/update")
    @AdminAndStandardAccessOnly
    ModelAndView updateLocation(
            @Validated @ModelAttribute("locationForm") EditLocationForm editForm,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            return view("/locations/edit")
                    .addObject("parents", getActiveParentLocationsForType(editForm.getType()))
                    .addObject("booleans", Booleans.VALUES);
        }

        Location location = locationService.findById(editForm.getId());
        if (location == null) {
            setDangerFlashMessage("Location does not exist", attributes);
            return redirect("/locations");
        }

        if (editForm.getParent() != null) {
            if (!editForm.getType().isRoot) {
                Location parentLocation = locationService.findActiveLocationById(editForm.getParent());
                if (parentLocation == null) {
                    setDangerFlashMessage(format("Selected %s is not currently active.",
                            editForm.getType().parent.description), attributes);
                    return view("/locations/edit")
                            .addObject("parents", getActiveParentLocationsForType(editForm.getType()))
                            .addObject("booleans", Booleans.VALUES);
                }
                if (!editForm.getType().isImmediateChildOf(parentLocation.getLocationType())) {
                    setDangerFlashMessage(
                            format(
                                    "Invalid location selection. %s must be directly under %s.",
                                    editForm.getType().description,
                                    editForm.getType().parent.description
                            ),
                            attributes
                    );
                    return view("/locations/edit")
                            .addObject("parents", getActiveParentLocationsForType(editForm.getType()))
                            .addObject("booleans", Booleans.VALUES);
                }
            }
        } else {
            if (!editForm.getType().isRoot) {
                result.addError(new FieldError("locationForm", "parent", format("%s is required.",
                        editForm.getType().parent.description)));
                return view("locations/edit")
                        .addObject("parents", getActiveParentLocationsForType(editForm.getType()))
                        .addObject("booleans", Booleans.VALUES);
            }
        }

        if (location.getCode() != editForm.getCode()) {
            Location conflict = locationService.findByCode(editForm.getCode());
            if (conflict != null) {
                ModelAndView view = view("locations/edit")
                        .addObject("parents", getActiveParentLocationsForType(editForm.getType()))
                        .addObject("booleans", Booleans.VALUES);
                return setDangerMessage(view, format("Selected code is already assigned to %s.", conflict.getName()));
            }
            location.setCode(editForm.getCode());
        }

        location.setName(editForm.getName());
        location.setParentId(editForm.getParent());
        location.setActive(editForm.getActive().value);

        locationService.save(location);

        setSuccessFlashMessage("Location successfully updated", attributes);

        if (location.getLocationType().isRoot) {
            return redirect("/locations");
        } else {
            return redirect(format("/locations/%d/sublocations", location.getParentId()));
        }
    }

    @GetMapping("/{location-id}/edit")
    @AdminAndStandardAccessOnly
    ModelAndView editLocation(@PathVariable("location-id") Long locationId,
                              @ModelAttribute("locationForm") EditLocationForm locationForm,
                              RedirectAttributes attributes) {
        Location location = locationService.findById(locationId);
        if (location == null) {
            setDangerFlashMessage("Location does not exist", attributes);
            return redirect("/locations");
        }
        locationForm.setId(locationId);
        locationForm.setName(location.getName());
        locationForm.setParent(location.getParentId());
        locationForm.setType(location.getLocationType());
        locationForm.setActive(Booleans.of(location.isActive()));
        locationForm.setCode(location.getCode());
        return view("locations/edit")
                .addObject("booleans", Booleans.VALUES)
                .addObject("locationType", terminologyService.getTerminologyByName(location.getLocationType().name()))
                .addObject("parentType", getLocationParentType(location.getLocationType()))
                .addObject("parents", getActiveParentLocationsForType(location.getLocationType()));
    }

    @AdminAndStandardAccessOnly
    @GetMapping(value = "/get-child-locations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SelectOptionItem>> getSubLocations(@RequestParam("id") Long parentId) {
        List<LocationCode> subLocations = locationService.getLocationCodesByParent(parentId);
        if (subLocations.isEmpty()) {
            return ResponseEntity
                    .ok().contentType(MediaType.APPLICATION_JSON)
                    .body(Collections.emptyList());
        }
        return ResponseEntity
                .ok().contentType(MediaType.APPLICATION_JSON)
                .body(toSelectOptions(subLocations));
    }
}

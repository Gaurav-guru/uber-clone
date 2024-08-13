package rs.ac.uns.ftn.transport.controller;

import com.google.common.base.Strings;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.transport.dto.DocumentDTO;
import rs.ac.uns.ftn.transport.dto.RidePageDTO;
import rs.ac.uns.ftn.transport.dto.VehicleDTO;
import rs.ac.uns.ftn.transport.dto.driver.DriverDTO;
import rs.ac.uns.ftn.transport.dto.driver.DriverPageDTO;
import rs.ac.uns.ftn.transport.dto.driver.DriverPasswordDTO;
import rs.ac.uns.ftn.transport.dto.ride.RideCreatedDTO;
import rs.ac.uns.ftn.transport.dto.workinghours.WorkingHoursDTO;
import rs.ac.uns.ftn.transport.dto.workinghours.WorkingHoursEndDTO;
import rs.ac.uns.ftn.transport.dto.workinghours.WorkingHoursPageDTO;
import rs.ac.uns.ftn.transport.dto.workinghours.WorkingHoursStartDTO;
import rs.ac.uns.ftn.transport.mapper.DocumentDTOMapper;
import rs.ac.uns.ftn.transport.mapper.VehicleDTOMapper;
import rs.ac.uns.ftn.transport.mapper.driver.DriverDTOMapper;
import rs.ac.uns.ftn.transport.mapper.driver.DriverPasswordDTOMapper;
import rs.ac.uns.ftn.transport.mapper.ride.RideCreatedDTOMapper;
import rs.ac.uns.ftn.transport.mapper.workinghours.WorkingHoursDTOMapper;
import rs.ac.uns.ftn.transport.mapper.workinghours.WorkingHoursStartDTOMapper;
import rs.ac.uns.ftn.transport.model.*;
import rs.ac.uns.ftn.transport.model.enumerations.DocumentType;
import rs.ac.uns.ftn.transport.service.interfaces.*;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping(value="api/driver")
public class DriverController {
    private final IDriverService driverService;
    private final IDocumentService documentService;
    private final IVehicleService vehicleService;
    private final ILocationService locationService;
    private final IWorkingHoursService workingHoursService;
    private final IRideService rideService;
    private final MessageSource messageSource;
    private final IImageService imageService;
    private final IDriverEditRequestService driverEditRequestService;
    private final IUserService userService;
    private final IRoleService roleService;

    public DriverController(IDriverService driverService,
                            IDocumentService documentService,
                            IVehicleService vehicleService,
                            ILocationService locationService,
                            IWorkingHoursService workingHoursService,
                            IRideService rideService,
                            MessageSource messageSource,
                            IImageService imageService,
                            IDriverEditRequestService driverEditRequestService,
                            IUserService userService,
                            IRoleService roleService) {
        this.driverService = driverService;
        this.documentService = documentService;
        this.vehicleService = vehicleService;
        this.locationService = locationService;
        this.workingHoursService = workingHoursService;
        this.rideService = rideService;
        this.messageSource = messageSource;
        this.imageService = imageService;
        this.driverEditRequestService = driverEditRequestService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDriver(@PathVariable Integer id) {
        try {
            Driver driver = driverService.findOne(id);
            return new ResponseEntity<>(DriverDTOMapper.fromDrivertoDTO(driver), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<DriverPageDTO> getDrivers(Pageable page) {
        Page<Driver> drivers = driverService.findAll(page);

        Set<DriverDTO> driverDTOs = drivers.stream()
                .map(DriverDTOMapper::fromDrivertoDTO)
                .collect(Collectors.toSet());

        return new ResponseEntity<>(new DriverPageDTO(drivers.getTotalElements(), driverDTOs), HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> saveDriver(@Valid @RequestBody DriverPasswordDTO dto) throws ConstraintViolationException {
        Driver driver = DriverPasswordDTOMapper.fromDTOtoDriver(dto);
        driver.setIsActive(false);
        if (driver.getProfilePicture() != null) {
            ResponseEntity<ResponseMessage> invalidProfilePicture = imageService.decodeAndValidateImage(driver.getProfilePicture());
            if (invalidProfilePicture != null) {
                return invalidProfilePicture;
            }
        }

        try {
            driver.setRoles(roleService.findByName("ROLE_DRIVER"));
            driver = driverService.save(driver);
            return new ResponseEntity<>(DriverDTOMapper.fromDrivertoDTO(driver), HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("user.emailExists", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateDriver(@PathVariable Integer id, @Valid @RequestBody DriverDTO driver) throws ConstraintViolationException {
        Driver driverToUpdate;
        try {
            driverToUpdate = driverService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        driverToUpdate.setName(driver.getName());
        driverToUpdate.setSurname(driver.getSurname());
        if (!Strings.isNullOrEmpty(driver.getProfilePicture())) {

            ResponseEntity<ResponseMessage> invalidProfilePicture = imageService.decodeAndValidateImage(driver.getProfilePicture());
            if (invalidProfilePicture != null) {
                return invalidProfilePicture;
            }

            driverToUpdate.setProfilePicture(driver.getProfilePicture());
        }
        if (!Strings.isNullOrEmpty(driver.getTelephoneNumber())) {
            driverToUpdate.setTelephoneNumber(driver.getTelephoneNumber());
        }
        driverToUpdate.setEmail(driver.getEmail());
        driverToUpdate.setAddress(driver.getAddress());

        try {
            driverToUpdate = driverService.save(driverToUpdate);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("user.emailExists", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(DriverDTOMapper.fromDrivertoDTO(driverToUpdate), HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/documents", consumes = "multipart/form-data")
    public ResponseEntity<?> saveDocument(@PathVariable Integer id,
                                          @RequestPart("documentImage") MultipartFile image,
                                          @RequestPart("name") String type) throws ConstraintViolationException {
        Driver driver;
        try {
            driver = driverService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        if (type.length() > 100) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("document.nameTooLong", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        if (image == null || image.isEmpty()) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("imageNull", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        if (!Objects.requireNonNull(image.getContentType()).startsWith("image/")) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("imageFormat", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        String imageString;
        try {
            imageString = Base64.getEncoder().encodeToString(image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Document document = new Document(DocumentType.getEnum(type), imageString, driver);

        document = documentService.save(document);
        return new ResponseEntity<>(DocumentDTOMapper.fromDocumenttoDTO(document), HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/documents", consumes = "application/json")
    public ResponseEntity<?> saveDocument(@PathVariable Integer id,
                                          @Valid @RequestBody DocumentDTO documentDTO) throws ConstraintViolationException {
        Driver driver;
        try {
            driver = driverService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        ResponseEntity<ResponseMessage> invalidImage = imageService.decodeAndValidateImage(documentDTO.getDocumentImage());
        if (invalidImage != null) {
            return invalidImage;
        }

        Document document = new Document(DocumentType.getEnum(documentDTO.getName()), documentDTO.getDocumentImage(), driver);

        document = documentService.save(document);
        return new ResponseEntity<>(DocumentDTOMapper.fromDocumenttoDTO(document), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/documents")
    public ResponseEntity<?> getDocuments(@PathVariable Integer id) {
        try {
            driverService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        Set<Document> documents = documentService.findAllByDriver_Id(id);

        Set<DocumentDTO> documentDTOs = documents.stream()
                                    .map(DocumentDTOMapper::fromDocumenttoDTO)
                                    .collect(Collectors.toSet());
        return new ResponseEntity<>(documentDTOs, HttpStatus.OK);
    }

    @DeleteMapping(value = "/document/{id}")
    @Transactional
    public ResponseEntity<String> deleteDocument(@PathVariable Integer id) {
        try {
            documentService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("document.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        documentService.deleteById(id);
        return new ResponseEntity<>("Driver document deleted successfully", HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/{id}/vehicle", consumes = "application/json")
    public ResponseEntity<?> saveVehicle(@PathVariable Integer id, @Valid @RequestBody VehicleDTO vehicleDTO) throws ConstraintViolationException {
        Driver driver;
        try {
            driver = driverService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        Vehicle vehicle = VehicleDTOMapper.fromDTOtoVehicle(vehicleDTO);
        vehicle.setDriver(driver);

        if (vehicle.getCurrentLocation() != null) {
            Location location = vehicle.getCurrentLocation();
            locationService.save(location);
        }

        if (vehicle.getPetTransport() == null) {
            vehicle.setPetTransport(false);
        }
        if (vehicle.getBabyTransport() == null) {
            vehicle.setBabyTransport(false);
        }

        vehicle = vehicleService.save(vehicle);

        driver.setVehicle(vehicle);
        driverService.save(driver);
        return new ResponseEntity<>(VehicleDTOMapper.fromVehicletoDTO(vehicle), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/vehicle")
    public ResponseEntity<?> getVehicle(@PathVariable Integer id) {
        Driver driver;
        try {
            driver = driverService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        Vehicle vehicle = driver.getVehicle();

        if (vehicle == null) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("vehicle.notFound", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(VehicleDTOMapper.fromVehicletoDTO(vehicle), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/vehicle", consumes = "application/json")
    public ResponseEntity<?> updateVehicle(@PathVariable Integer id, @Valid @RequestBody VehicleDTO vehicleDTO) throws ConstraintViolationException {
        Driver driver;
        try {
            driver = driverService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        Vehicle oldVehicle = driver.getVehicle();

        if (oldVehicle == null) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("vehicle.notFound", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        Vehicle newVehicle = VehicleDTOMapper.fromDTOtoVehicle(vehicleDTO);

        oldVehicle.setVehicleType(newVehicle.getVehicleType());
        oldVehicle.setModel(newVehicle.getModel());
        oldVehicle.setLicenseNumber(newVehicle.getLicenseNumber());

        if (newVehicle.getCurrentLocation() != null) {
            oldVehicle.setCurrentLocation(newVehicle.getCurrentLocation());
            locationService.save(oldVehicle.getCurrentLocation());
        }

        oldVehicle.setPassengerSeats(newVehicle.getPassengerSeats());
        if (newVehicle.getBabyTransport() != null) {
            oldVehicle.setBabyTransport(newVehicle.getBabyTransport());
        }
        if (newVehicle.getPetTransport() != null) {
            oldVehicle.setPetTransport(newVehicle.getPetTransport());
        }

        oldVehicle = vehicleService.save(oldVehicle);
        return new ResponseEntity<>(VehicleDTOMapper.fromVehicletoDTO(oldVehicle), HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/working-hour", consumes = "application/json")
    public ResponseEntity<?> saveWorkingHours(@PathVariable Integer id, @Valid @RequestBody WorkingHoursStartDTO workingHoursDTO) throws ConstraintViolationException {
        Driver driver;
        try {
            driver = driverService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        if (driver.getVehicle() == null) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("workingHour.start.vehicle.notFound", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        if (workingHoursDTO.getStart().isAfter(LocalDateTime.now())) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("workingHour.start.future", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        WorkingHours workingHours = WorkingHoursStartDTOMapper.fromDTOToWorkingHoursStart(workingHoursDTO);
        workingHours.setDriver(driver);
        workingHours.setEnd(workingHours.getStart());

        try {
            workingHours = workingHoursService.start(workingHours);
        } catch (ResponseStatusException e) {
            if (Objects.equals(e.getReason(), "ongoing")) {
                return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("workingHour.start.ongoing", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("workingHour.start.limit", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(WorkingHoursDTOMapper.fromWorkingHoursToDTO(workingHours), HttpStatus.OK);
    }

    @GetMapping(value = "/working-hour/{workingHourId}")
    public ResponseEntity<?> getWorkingHour(@PathVariable Integer workingHourId) {
        WorkingHours workingHours;
        try {
            workingHours = workingHoursService.findOne(workingHourId);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("workingHour.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(WorkingHoursDTOMapper.fromWorkingHoursToDTO(workingHours), HttpStatus.OK);
    }

    @PutMapping(value = "/working-hour/{workingHourId}", consumes = "application/json")
    public ResponseEntity<?> updateWorkingHour(@PathVariable Integer workingHourId, @Valid @RequestBody WorkingHoursEndDTO workingHoursDTO) throws ConstraintViolationException {
        WorkingHours workingHours;
        try {
            workingHours = workingHoursService.findOne(workingHourId);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("workingHour.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        if (workingHoursDTO.getEnd().isBefore(workingHours.getStart())) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("workingHour.end.beforeStart", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        if (workingHoursDTO.getEnd().isAfter(LocalDateTime.now())) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("workingHour.end.future", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        if (workingHours.getDriver().getVehicle() == null) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("workingHour.end.vehicle.notFound", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        if (!workingHours.getStart().equals(workingHours.getEnd())) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("workingHour.end.notOngoing", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        workingHours.setEnd(workingHoursDTO.getEnd());

        workingHours = workingHoursService.end(workingHours);
        return new ResponseEntity<>(WorkingHoursDTOMapper.fromWorkingHoursToDTO(workingHours), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/working-hour")
    public ResponseEntity<?> getWorkingHours(Pageable page,
                                                               @PathVariable Integer id,
                                                               @RequestParam(value = "from", required = false) LocalDateTime from,
                                                               @RequestParam(value = "to", required = false) LocalDateTime to) {
        try {
            driverService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        Page<WorkingHours> workingHours;

        if (from == null && to == null) {
            workingHours = workingHoursService.findAllByDriver_Id(id, page);
        } else if (from != null && to == null) {
            workingHours = workingHoursService.findAllByDriver_IdAndStartIsAfter(id, from, page);
        } else if (from == null) {
            workingHours = workingHoursService.findAllByDriver_IdAndEndIsBefore(id, to, page);
        } else {
            workingHours = workingHoursService.findAllByDriver_IdAndStartIsAfterAndEndIsBefore(id, from, to, page);
        }

        Set<WorkingHoursDTO> workingHoursDTOs = workingHours.stream()
                .map(WorkingHoursDTOMapper::fromWorkingHoursToDTO)
                .collect(Collectors.toSet());

        return new ResponseEntity<>(new WorkingHoursPageDTO(workingHours.getTotalElements(), workingHoursDTOs), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/ride")
    public ResponseEntity<?> getRides(Pageable page,
                                                @PathVariable Integer id,
                                                @RequestParam(value = "from", required = false) LocalDateTime from,
                                                @RequestParam(value = "to", required = false) LocalDateTime to)
    {
        try {
            driverService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        if (from != null && from.isAfter(LocalDateTime.now())) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("from.future", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        Page<Ride> rides;

        if (from == null && to == null) {
            rides = rideService.findAllByDriver_Id(id, page);
        } else if (from != null && to == null) {
            rides = rideService.findAllByDriver_IdAndStartTimeIsAfter(id, from, page);
        } else if (from == null) {
            rides = rideService.findAllByDriver_IdAndEndTimeIsBefore(id, to, page);
        } else {
            rides = rideService.findAllByDriver_IdAndStartTimeIsAfterAndEndTimeIsBefore(id, from, to, page);
        }

        Set<RideCreatedDTO> rideDTOs = rides.stream()
                .map(RideCreatedDTOMapper::fromRideToDTO)
                .collect(Collectors.toSet());

        return new ResponseEntity<>(new RidePageDTO(rides.getTotalElements(), rideDTOs), HttpStatus.OK);
    }

    @PostMapping(value = "/edit-request", consumes = "application/json")
    public ResponseEntity<?> saveDriverEditRequest(@Valid @RequestBody DriverEditRequest request) throws ConstraintViolationException {
        Driver actualDriver;
        try {
            actualDriver = driverService.findOne(request.getDriverId());
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("driver.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        if (actualDriver.getName().equals(request.getName()) &&
                actualDriver.getSurname().equals(request.getSurname()) &&
                (request.getProfilePicture() == null ||
                        actualDriver.getProfilePicture().equals(request.getProfilePicture())) &&
                actualDriver.getTelephoneNumber().equals(request.getTelephoneNumber()) &&
                actualDriver.getEmail().equals(request.getEmail()) &&
                actualDriver.getAddress().equals(request.getAddress())) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("driver.editRequest.noChange", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        // check if email is already taken
        User user = userService.findByEmail(request.getEmail());
        if (user != null && !user.getId().equals(actualDriver.getId())) {
            return new ResponseEntity<>(new ResponseMessage(messageSource.getMessage("user.emailExists", null, Locale.getDefault())), HttpStatus.BAD_REQUEST);
        }

        // validate profile picture
        if (request.getProfilePicture() != null) {
            ResponseEntity<ResponseMessage> invalidProfilePicture = imageService.decodeAndValidateImage(request.getProfilePicture());
            if (invalidProfilePicture != null) {
                return invalidProfilePicture;
            }
        }

        request.setReviewed(false);
        request.setApproved(false);
        request.setDateOfCreation(LocalDateTime.now());

        DriverEditRequest existingRequest = driverEditRequestService.findByDriverId(request.getDriverId());
        if (existingRequest != null) {
            request.setId(existingRequest.getId());
        }

        return new ResponseEntity<>(driverEditRequestService.save(request), HttpStatus.OK);
    }
}

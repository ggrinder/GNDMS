enable_providerstagein() {
RW="700"

moni call -v .dspace.SetupSliceKind "sliceKind:'http://www.c3grid.de/G2/SliceKind/Staging'; sliceKindMode:$RW; uniqueDirName:RW; mode: $MODE"

moni call -v .dspace.SetupSubspace  "subspace:'{http://www.c3grid.de/G2/Subspace}ProviderStaging'; \
	path:'$STAGING_AREA_PATH'; 
	gsiFtpPath:'$STAGING_AREA_GSI_FTP_URL'; \
	visible:true; \
	size:'$STAGING_AREA_SIZE'; \
	mode:'$MODE'"

moni call -v .dspace.AssignSliceKind "subspace:'{http://www.c3grid.de/G2/Subspace}ProviderStaging'; sliceKind: http://www.c3grid.de/G2/SliceKind/Staging; mode:'$ADDMODE'"
moni call -v .dspace.AssignSliceKind "subspace:'{http://www.c3grid.de/G2/Subspace}ProviderStaging'; sliceKind: http://www.c3grid.de/G2/SliceKind/DMS; mode:'$ADDMODE'"
moni call -v .dspace.AssignSliceKind "subspace:'{http://www.c3grid.de/G2/Subspace}ProviderStaging'; sliceKind: http://www.c3grid.de/G2/SliceKind/DMS_RW; mode:'$ADDMODE'"

moni call -v .gorfx.SetupOfferType "offerType: 'http://www.c3grid.de/ORQTypes/ProviderStageIn'; orqType: '{http://gndms.zib.de/c3grid/types}ProviderStageInORQT'; resType: '{http://gndms.zib.de/c3grid/types}ProviderStageInResultT'; calcFactory: de.zib.gndms.logic.model.gorfx.c3grid.ProviderStageInORQFactory; taskActionFactory: de.zib.gndms.logic.model.gorfx.c3grid.ProviderStageInActionFactory; mode:'$MODE'"

moni call -v .gorfx.ConfigOfferType "offerType: 'http://www.c3grid.de/ORQTypes/ProviderStageIn'; \
	cfgOutFormat: 'PRINT_OK'; \
	subspace: '{http://www.c3grid.de/G2/Subspace}ProviderStaging'; \
	sliceKind: 'http://www.c3grid.de/G2/SliceKind/Staging'; \
	stagingClass: 'de.zib.gndms.logic.model.gorfx.c3grid.ExternalProviderStageInAction'; \
	estimationClass: 'de.zib.gndms.logic.model.gorfx.c3grid.ExternalProviderStageInORQCalculator'; \
	stagingCommand: '$STAGING_COMMAND'; \
	estimationCommand: '$ESTIMATION_COMMAND'; \
	cancelCommand: '$CANCEL_COMMAND'; \
	scriptIoFormat: '$SCRIPT_IO_FORMAT'"
}
# vim:tw=0

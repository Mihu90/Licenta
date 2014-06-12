/**
 * Contains the definition of the buffer used for allocating SRAM space
 * for the MTB trace.
 */

#if (defined(__SA_MTB_SIZE) && (__SA_MTB_SIZE > 0))
    // Alignment of the MTB buffer
    #define SA_MTB_ALIGNEMENT  64  
    unsigned char __attribute__((section (".mtb_buf"))) mtb_buf[__SA_MTB_SIZE] __attribute__ ((aligned (SA_MTB_ALIGNEMENT)));
#endif

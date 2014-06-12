/* ###################################################################
**     THIS COMPONENT MODULE IS GENERATED BY THE TOOL. DO NOT MODIFY IT.
**     Filename    : PwmLdd3.c
**     Project     : ProcessorExpert
**     Processor   : MKL46Z256VMC4
**     Component   : PWM_LDD
**     Version     : Component 01.013, Driver 01.03, CPU db: 3.00.000
**     Compiler    : GNU C Compiler
**     Date/Time   : 2014-01-07, 01:22, # CodeGen: 200
**     Abstract    :
**          This component implements a pulse-width modulation generator
**          that generates signal with variable duty and fixed cycle.
**          This PWM component provides a high level API for unified
**          hardware access to various timer devices using the TimerUnit
**          component.
**     Settings    :
**          Component name                                 : PwmLdd3
**          Period device                                  : TPM1_MOD
**          Duty device                                    : TPM1_C1V
**          Output pin                                     : LCD_P1/ADC0_SE9/TSI0_CH6/PTB1/I2C0_SDA/TPM1_CH1
**          Output pin signal                              : 
**          Counter                                        : TPM1_CNT
**          Interrupt service/event                        : Enabled
**            Interrupt                                    : INT_TPM1
**            Interrupt priority                           : medium priority
**            Iterations before action/event               : 1
**          Period                                         : 3.125 ms
**          Starting pulse width                           : 3.124952 ms
**          Initial polarity                               : low
**          Initialization                                 : 
**            Enabled in init. code                        : yes
**            Auto initialization                          : yes
**            Event mask                                   : 
**              OnEnd                                      : Enabled
**          CPU clock/configuration selection              : 
**            Clock configuration 0                        : This component enabled
**            Clock configuration 1                        : This component disabled
**            Clock configuration 2                        : This component disabled
**            Clock configuration 3                        : This component disabled
**            Clock configuration 4                        : This component disabled
**            Clock configuration 5                        : This component disabled
**            Clock configuration 6                        : This component disabled
**            Clock configuration 7                        : This component disabled
**          Referenced components                          : 
**            Linked component                             : TU1
**     Contents    :
**         Init      - LDD_TDeviceData* PwmLdd3_Init(LDD_TUserData *UserDataPtr);
**         SetRatio8 - LDD_TError PwmLdd3_SetRatio8(LDD_TDeviceData *DeviceDataPtr, uint8_t Ratio);
**         SetDutyUS - LDD_TError PwmLdd3_SetDutyUS(LDD_TDeviceData *DeviceDataPtr, uint16_t Time);
**         SetDutyMS - LDD_TError PwmLdd3_SetDutyMS(LDD_TDeviceData *DeviceDataPtr, uint16_t Time);
**
**     Copyright : 1997 - 2013 Freescale Semiconductor, Inc. All Rights Reserved.
**     SOURCE DISTRIBUTION PERMISSIBLE as directed in End User License Agreement.
**     
**     http      : www.freescale.com
**     mail      : support@freescale.com
** ###################################################################*/
/*!
** @file PwmLdd3.c
** @version 01.03
** @brief
**          This component implements a pulse-width modulation generator
**          that generates signal with variable duty and fixed cycle.
**          This PWM component provides a high level API for unified
**          hardware access to various timer devices using the TimerUnit
**          component.
*/         
/*!
**  @addtogroup PwmLdd3_module PwmLdd3 module documentation
**  @{
*/         

/* MODULE PwmLdd3. */

#include "IN2.h"
#include "PwmLdd3.h"
#include "FreeRTOS.h" /* FreeRTOS interface */

#ifdef __cplusplus
extern "C" {
#endif 

typedef struct {
  LDD_TDeviceData *LinkedDeviceDataPtr;
  bool EnUser;                         /* Enable/Disable device */
  uint16_t RatioStore;                 /* Ratio of L-level to H-level */
  LDD_TUserData *UserDataPtr;          /* RTOS device data structure */
} PwmLdd3_TDeviceData;

typedef PwmLdd3_TDeviceData *PwmLdd3_TDeviceDataPtr; /* Pointer to the device data structure. */

/* {FreeRTOS RTOS Adapter} Static object used for simulation of dynamic driver memory allocation */
static PwmLdd3_TDeviceData DeviceDataPrv__DEFAULT_RTOS_ALLOC;

#define CHANNEL 0x01U
#define AVAILABLE_EVENTS_MASK (LDD_TEventMask)(LDD_PWM_ON_END)
/* Internal method prototypes */
static void SetRatio(LDD_TDeviceData *DeviceDataPtr);
/*
** ===================================================================
**     Method      :  PwmLdd3_Init (component PWM_LDD)
*/
/*!
**     @brief
**         Initializes the device. Allocates memory for the device data
**         structure, allocates interrupt vectors and sets interrupt
**         priority, sets pin routing, sets timing, etc. If the
**         property ["Enable in init. code"] is set to "yes" value then
**         the device is also enabled (see the description of the
**         [Enable] method). In this case the [Enable] method is not
**         necessary and needn't to be generated. This method can be
**         called only once. Before the second call of Init the [Deinit]
**         must be called first.
**     @param
**         UserDataPtr     - Pointer to the user or
**                           RTOS specific data. This pointer will be
**                           passed as an event or callback parameter.
**     @return
**                         - Pointer to the dynamically allocated private
**                           structure or NULL if there was an error.
*/
/* ===================================================================*/
LDD_TDeviceData* PwmLdd3_Init(LDD_TUserData *UserDataPtr)
{
  /* Allocate device structure */
  PwmLdd3_TDeviceData *DeviceDataPrv;
  /* {FreeRTOS RTOS Adapter} Driver memory allocation: Dynamic allocation is simulated by a pointer to the static object */
  DeviceDataPrv = &DeviceDataPrv__DEFAULT_RTOS_ALLOC;
  DeviceDataPrv->UserDataPtr = UserDataPtr; /* Store the RTOS device structure */
  DeviceDataPrv->EnUser = TRUE;        /* Set the flag "device enabled" */
  DeviceDataPrv->RatioStore = 0xFFFFU; /* Ratio after initialization */
  /* Registration of the device structure */
  PE_LDD_RegisterDeviceStructure(PE_LDD_COMPONENT_PwmLdd3_ID,DeviceDataPrv);
  DeviceDataPrv->LinkedDeviceDataPtr = TU1_Init((LDD_TUserData *)NULL);
  if (DeviceDataPrv->LinkedDeviceDataPtr == NULL) { /* Is initialization of TimerUnit unsuccessful? */
    /* Unregistration of the device structure */
    PE_LDD_UnregisterDeviceStructure(PE_LDD_COMPONENT_PwmLdd3_ID);
    /* Deallocation of the device structure */
    /* {FreeRTOS RTOS Adapter} Driver memory deallocation: Dynamic allocation is simulated, no deallocation code is generated */
    return NULL;                       /* If so, then the PWM initialization is also unsuccessful */
  }
  return ((LDD_TDeviceData *)DeviceDataPrv); /* Return pointer to the device data structure */
}

/*
** ===================================================================
**     Method      :  PwmLdd3_SetRatio8 (component PWM_LDD)
*/
/*!
**     @brief
**         This method sets a new duty-cycle ratio. Ratio is expressed
**         as an 8-bit unsigned integer number. 0 - FF value is
**         proportional to ratio 0 - 100%. The method is available
**         only if it is not selected list of predefined values in
**         [Starting pulse width] property. 
**         Note: Calculated duty depends on the timer capabilities and
**         on the selected period.
**     @param
**         DeviceDataPtr   - Device data structure
**                           pointer returned by [Init] method.
**     @param
**         Ratio           - Ratio to set. 0 - 255 value is
**                           proportional to ratio 0 - 100%
**     @return
**                         - Error code, possible codes:
**                           ERR_OK - OK
**                           ERR_SPEED - The component does not work in
**                           the active clock configuration
*/
/* ===================================================================*/
LDD_TError PwmLdd3_SetRatio8(LDD_TDeviceData *DeviceDataPtr, uint8_t Ratio)
{
  PwmLdd3_TDeviceData *DeviceDataPrv = (PwmLdd3_TDeviceData *)DeviceDataPtr;

  DeviceDataPrv->RatioStore = (uint16_t)Ratio << 8U; /* Store new value of the ratio */
  SetRatio(DeviceDataPtr);
  return ERR_OK;
}

/*
** ===================================================================
**     Method      :  PwmLdd3_SetDutyUS (component PWM_LDD)
*/
/*!
**     @brief
**         This method sets the new duty value of the output signal.
**         The duty is expressed in microseconds as a 16-bit unsigned
**         integer number. The method is available only if it is not
**         selected list of predefined values in [Starting pulse width]
**         property.
**     @param
**         DeviceDataPtr   - Device data structure
**                           pointer returned by [Init] method.
**     @param
**         Time            - Duty to set [in microseconds]
**     @return
**                         - Error code, possible codes:
**                           ERR_OK - OK
**                           ERR_SPEED - The component does not work in
**                           the active clock configuration
**                           ERR_MATH - Overflow during evaluation
**                           ERR_PARAM_RANGE - Parameter out of range
*/
/* ===================================================================*/
LDD_TError PwmLdd3_SetDutyUS(LDD_TDeviceData *DeviceDataPtr, uint16_t Time)
{
  PwmLdd3_TDeviceData *DeviceDataPrv = (PwmLdd3_TDeviceData *)DeviceDataPtr;
  LDD_TimerUnit_Tfloat rtval;          /* Result of multiplication */

  /* Time test - this test can be disabled by setting the "Ignore range checking"
     property to the "yes" value in the "Configuration inspector" */
  if (Time > 0x0C35U) {                /* Is the given value out of range? */
    return ERR_PARAM_RANGE;            /* If yes then error */
  }
  rtval = Time * 20.97152F;            /* Multiply given value and actual clock configuration coefficient */
  if (rtval > 0xFFFFUL) {              /* Is the result greater than 65535 ? */
    DeviceDataPrv->RatioStore = 0xFFFFU; /* If yes then use maximal possible value */
  }
  else {
    DeviceDataPrv->RatioStore = (uint16_t)rtval;
  }
  SetRatio(DeviceDataPtr);             /* Calculate and set up new appropriate values of the duty register */
  return ERR_OK;                       /* OK */
}

/*
** ===================================================================
**     Method      :  PwmLdd3_SetDutyMS (component PWM_LDD)
*/
/*!
**     @brief
**         This method sets the new duty value of the output signal.
**         The duty is expressed in milliseconds as a 16-bit unsigned
**         integer number. The method is available only if it is not
**         selected list of predefined values in [Starting pulse width]
**         property.
**     @param
**         DeviceDataPtr   - Device data structure
**                           pointer returned by [Init] method.
**     @param
**         Time            - Duty to set [in milliseconds]
**     @return
**                         - Error code, possible codes:
**                           ERR_OK - OK
**                           ERR_SPEED - The component does not work in
**                           the active clock configuration
**                           ERR_MATH - Overflow during evaluation
**                           ERR_PARAM_RANGE - Parameter out of range
*/
/* ===================================================================*/
LDD_TError PwmLdd3_SetDutyMS(LDD_TDeviceData *DeviceDataPtr, uint16_t Time)
{
  PwmLdd3_TDeviceData *DeviceDataPrv = (PwmLdd3_TDeviceData *)DeviceDataPtr;
  LDD_TimerUnit_Tfloat rtval;          /* Result of multiplication */

  /* Time test - this test can be disabled by setting the "Ignore range checking"
     property to the "yes" value in the "Configuration inspector" */
  if (Time > 0x03U) {                  /* Is the given value out of range? */
    return ERR_PARAM_RANGE;            /* If yes then error */
  }
  rtval = Time * 20971.52F;            /* Multiply given value and actual clock configuration coefficient */
  if (rtval > 0xFFFFUL) {              /* Is the result greater than 65535 ? */
    DeviceDataPrv->RatioStore = 0xFFFFU; /* If yes then use maximal possible value */
  }
  else {
    DeviceDataPrv->RatioStore = (uint16_t)rtval;
  }
  SetRatio(DeviceDataPtr);             /* Calculate and set up new appropriate values of the duty register */
  return ERR_OK;                       /* OK */
}

/*
** ===================================================================
**     Method      :  PwmLdd3_OnCounterRestart (component PWM_LDD)
**
**     Description :
**         Called if counter overflow/underflow or counter is 
**         reinitialized by modulo or compare register matching. 
**         OnCounterRestart event and Timer unit must be enabled. See <a 
**         href="UntitledMethods.html#SetEventMask">SetEventMask</a> and 
**         <a href="UntitledMethods.html#GetEventMask">GetEventMask</a> 
**         methods.This event is available only if a <a 
**         href="UntitledProperties.html#IntServiceCounter">Interrupt</a> 
**         is enabled. The event services the event of the inherited 
**         component and eventually invokes other events.
**         This method is internal. It is used by Processor Expert only.
** ===================================================================
*/
void TU1_OnCounterRestart1(LDD_TUserData *UserDataPtr)
{
  PwmLdd3_TDeviceData *DeviceDataPrv = PE_LDD_DeviceDataList[PE_LDD_COMPONENT_PwmLdd3_ID];

  PwmLdd3_OnEnd(DeviceDataPrv->UserDataPtr); /* Invoke OnEnd event */
}

/*
** ===================================================================
**     Method      :  SetRatio (component PWM_LDD)
**
**     Description :
**         This method calculates new value of duty compare register from 
**         variable RatioStore.
**         This method is internal. It is used by Processor Expert only.
** ===================================================================
*/
static void SetRatio(LDD_TDeviceData *DeviceDataPtr)
{
  PwmLdd3_TDeviceData *DeviceDataPrv = (PwmLdd3_TDeviceData*)DeviceDataPtr;
  uint16_t Period;
  uint16_t Duty;

  (void)TU1_GetPeriodTicks(DeviceDataPrv->LinkedDeviceDataPtr, &Period);
  if (Period == 0U) {
    Duty = DeviceDataPrv->RatioStore;
  }
  else {
    Duty = (uint16_t)((((uint32_t)(Period) * DeviceDataPrv->RatioStore) + 0x8000) >> 0x10);
  }
  (void)TU1_SetOffsetTicks(DeviceDataPrv->LinkedDeviceDataPtr, CHANNEL, Duty);
}
/* END PwmLdd3. */

#ifdef __cplusplus
}  /* extern "C" */
#endif 

/*!
** @}
*/
/*
** ###################################################################
**
**     This file was created by Processor Expert 10.3 [05.08]
**     for the Freescale Kinetis series of microcontrollers.
**
** ###################################################################
*/

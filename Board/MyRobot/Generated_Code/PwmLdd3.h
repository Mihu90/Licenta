/* ###################################################################
**     THIS COMPONENT MODULE IS GENERATED BY THE TOOL. DO NOT MODIFY IT.
**     Filename    : PwmLdd3.h
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
** @file PwmLdd3.h
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

#ifndef __PwmLdd3_H
#define __PwmLdd3_H

/* MODULE PwmLdd3. */

/* Include shared modules, which are used for whole project */
#include "PE_Types.h"
#include "PE_Error.h"
#include "PE_Const.h"
#include "IO_Map.h"
/* Include inherited beans */
#include "TU1.h"
#include "TPM_PDD.h"

#include "Cpu.h"

#ifdef __cplusplus
extern "C" {
#endif 


#define PwmLdd3_PERIOD_VALUE 0x00UL    /* Initial period value in ticks of the timer. */
#define PwmLdd3_PERIOD_VALUE_0 0x00UL  /* Period value in ticks of the timer in clock configuration 0. */

/*! Peripheral base address of a device allocated by the component. This constant can be used directly in PDD macros. */
#define PwmLdd3_PRPH_BASE_ADDRESS  0x40039000U
  
/*! Device data structure pointer used when auto initialization property is enabled. This constant can be passed as a first parameter to all component's methods. */
#define PwmLdd3_DeviceData  ((LDD_TDeviceData *)PE_LDD_GetDeviceStructure(PE_LDD_COMPONENT_PwmLdd3_ID))

/* Methods configuration constants - generated for all enabled component's methods */
#define PwmLdd3_Init_METHOD_ENABLED    /*!< Init method of the component PwmLdd3 is enabled (generated) */
#define PwmLdd3_SetRatio8_METHOD_ENABLED /*!< SetRatio8 method of the component PwmLdd3 is enabled (generated) */
#define PwmLdd3_SetDutyUS_METHOD_ENABLED /*!< SetDutyUS method of the component PwmLdd3 is enabled (generated) */
#define PwmLdd3_SetDutyMS_METHOD_ENABLED /*!< SetDutyMS method of the component PwmLdd3 is enabled (generated) */

/* Events configuration constants - generated for all enabled component's events */
#define PwmLdd3_OnEnd_EVENT_ENABLED    /*!< OnEnd event of the component PwmLdd3 is enabled (generated) */



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
LDD_TDeviceData* PwmLdd3_Init(LDD_TUserData *UserDataPtr);

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
LDD_TError PwmLdd3_SetRatio8(LDD_TDeviceData *DeviceDataPtr, uint8_t Ratio);

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
LDD_TError PwmLdd3_SetDutyUS(LDD_TDeviceData *DeviceDataPtr, uint16_t Time);

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
LDD_TError PwmLdd3_SetDutyMS(LDD_TDeviceData *DeviceDataPtr, uint16_t Time);

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
void TU1_OnCounterRestart1(LDD_TUserData *UserDataPtr);

/* END PwmLdd3. */

#ifdef __cplusplus
}  /* extern "C" */
#endif 

#endif 
/* ifndef __PwmLdd3_H */
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
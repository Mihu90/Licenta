/* ###################################################################
**     THIS COMPONENT MODULE IS GENERATED BY THE TOOL. DO NOT MODIFY IT.
**     Filename    : RBT1.c
**     CDE edition : Standard
**     Project     : ProcessorExpert
**     Processor   : MKL46Z256VMC4
**     Component   : Robot
**     Version     : Component 00.029, Driver 00.00, CPU db: 3.00.000
**     Compiler    : GNU C Compiler
**     Date/Time   : 2014-05-05, 19:26, # CodeGen: 213
**     Abstract    :
**
**     Settings    :
**     Contents    :
**         Init           - 
**         CreateAppTasks - 
**         ReceiveTask    - 
**         BatteryTask    - 
**         Run            - 
**         ReadLine       - 
**         ReadCharBT     - 
**         SendCharBT     - 
**         ParseLine      - 
**         ParseCommand   - 
**
**     For non-commercial use only.
**     (c) Copyright Surdeanu Mihai,
**     http://mihaisurdeanu.ro
**     mail: contact@mihaisurdeanu.ro
** ###################################################################*/
/*!
** @file RBT1.c
** @version 00.00
** @brief
**
*/         
/*!
**  @addtogroup RBT1_module RBT1 module documentation
**  @{
*/         

/* MODULE RBT1. */

#include "RBT1.h"

xSemaphoreHandle xSemaphore;

/*
** ===================================================================
**     Method      :  RBT1_ParseCommand (component Robot)
**     Description :
**          
**     Parameters  :
**         NAME            - DESCRIPTION
**     Returns     :
**         void            - 
** ===================================================================
*/
static void RBT1_ParseCommand(unsigned char *cmd, ConstStdIOType *io)
{
    if (cmd[0] != '>' && cmd[0] != '<') {
        goto invalidCommand;
    }
    
        if (cmd[0] == '>') {
        IN1_SetRatio8(255 - cmd[1]);
                IN3_SetRatio8(255 - cmd[2]);
        	
                IN2_SetRatio8(255);
                IN4_SetRatio8(255);
    } else if (cmd[0] == '<') {
                IN1_SetRatio8(255);
                IN3_SetRatio8(255);
        	
                IN2_SetRatio8(255 - cmd[1]);
                IN4_SetRatio8(255 - cmd[2]);
        } else {
invalidCommand:
        // Markeaza comanda ca fiind invalida
        cmd[0] = '-';
        goto sendResponse;  
    }
    
    // Marcheaza comanda ca una valida
    cmd[0] = '+';
    
    // Trimite raspunsul la utilizator
sendResponse:
	xSemaphoreTake(xSemaphore, portMAX_DELAY);
    while(*cmd != 0) {
        io->stdOut(*cmd++);
    }
    xSemaphoreGive(xSemaphore);
}

/*
** ===================================================================
**     Method      :  RBT1_ParseLine (component Robot)
**     Description :
**          
**     Parameters  :
**         NAME            - DESCRIPTION
**     Returns     :
**         void            - 
** ===================================================================
*/
static void RBT1_ParseLine(byte *cmdBuf, size_t cmdBufSize, ConstStdIOType *io)
{
    size_t len = 0;
    uint8_t readed;

    // Buffer-ul trebuie initializat inainte de a fi utilizat
    len = strlen((const char *)cmdBuf);
    if ((readed = RBT1_ReadLine(cmdBuf + len, cmdBufSize - len, io)) > 0) {
        // Dimensiunea buffer-ul este calculata dinamic
        len += readed;
        if (cmdBuf[0] != 0 && cmdBuf[len - 1] == '\n') {
            cmdBuf[len] = 0;
            RBT1_ParseCommand(cmdBuf, io);
            cmdBuf[0] = 0;
        }
        // Continua procesul de appending
    }
}

/*
** ===================================================================
**     Method      :  RBT1_SendCharBT (component Robot)
**     Description :
**          
**     Parameters  :
**         NAME            - DESCRIPTION
**     Returns     :
**         void            - 
** ===================================================================
*/
static void RBT1_SendCharBT(byte ch)
{
    while (BlueSerial_SendChar((uint8_t)ch) == ERR_TXFULL);
}

/*
** ===================================================================
**     Method      :  RBT1_ReadCharBT (component Robot)
**     Description :
**          
**     Parameters  :
**         NAME            - DESCRIPTION
**     Returns     :
**         void            - 
** ===================================================================
*/
static void RBT1_ReadCharBT(byte* ch)
{
    if (BlueSerial_RecvChar((uint8_t *)ch) != ERR_OK) {
        *ch = 0;
    }
}

/*
** ===================================================================
**     Method      :  RBT1_ReadLine (component Robot)
**     Description :
**          
**     Parameters  :
**         NAME            - DESCRIPTION
**     Returns     :
**         void            - 
** ===================================================================
*/
static uint8_t RBT1_ReadLine(byte *buf, size_t bufSize, ConstStdIOType *io)
{
    // Cate caractere sunt citite?
    uint8_t readed = 0;

    if (BlueSerial_GetCharsInRxBuf() != 0U) {
        for( ; readed < bufSize; ) {
            // Citeste un caracter si adauga-l la string-ul initial
            io->stdIn(buf);
            // Este ceva in bufferul RX?
            if (*buf == 0) {
                break;
            }
            buf++; readed++;
            // Fiecare comanda trebuie sa aiba ca si terminator "\n"
            if (*buf == '\n') {
                break;
            }
        }
        *buf = 0;
    }
    return readed;
}

/*
** ===================================================================
**     Method      :  RBT1_Run (component Robot)
**
**     Description :
**         This method is internal. It is used by Processor Expert only.
** ===================================================================
*/
void RBT1_Run()
{
    // Create all tasks needed
    RBT1_CreateAppTasks();
}

/*
** ===================================================================
**     Method      :  RBT1_SendTask (component Robot)
**
**     Description :
**         This method is internal. It is used by Processor Expert only.
** ===================================================================
*/
static void RBT1_BatteryTask(void* param)
{
	// Mesajul care va fi trimis catre telefonul mobil va avea maxim 16 caractere
    unsigned char msg[16];
    // Identificator de comanda
    msg[0] = '$';
    unsigned char *current;
    uint16_t value, sum;
    uint8_t channel;
    
    // La fiecare 300 de millisecunde se trimit informatiile catre telefonul mobil
    // cu privire la distanta din fata masinii.
    for (;;) {
    	sum = 0;
    	current = msg + 2;
    	// Se masoara pe rand pentru toate cele 3 pozitii
    	for (channel = 0; channel < 3; channel++) {
    		// Masuram distanta si adaugam datele la mesajul ce se va trimite
    		value = MeasureMillimeters(channel);
    		sum += value;
    		do {
    			*current++ = '0' + value % 10;
    			value = value / 10;
    		} while (value != 0);
    		*current++ = '|';    		
    	}
    	// Adaugam si linia noua la finalul comenzii
    	*(current - 1) = '\n';
    	*current = 0;
    	
    	// Foloseste si un simplu CRC pentru a valida datele
		msg[1] = sum % 115 + 11;
		
    	// Un semafor este folosit pe post de mutex pentru a putea
		// trimite mesaje.
    	xSemaphoreTake(xSemaphore, portMAX_DELAY);
    	current = msg;
    	while (*current != 0) {
    		RBT1_SendCharBT(*current++);  
    	}
    	xSemaphoreGive(xSemaphore);
    	
    	// Mai asteptam putin pana la urmatoarea trimitere a datelor
    	OS_vTaskDelay(300 / portTICK_RATE_MS);
    }
}

/*
** ===================================================================
**     Method      :  RBT1_ReceiveTask (component Robot)
**
**     Description :
**         This method is internal. It is used by Processor Expert only.
** ===================================================================
*/
static void RBT1_ReceiveTask(void* param)
{
    // Parametrul este neutilizat
    (void)param;
    // O comanda poate avea maxim 32 de caractere
    unsigned char buf[32];
    static ConstStdIOType BTstdio = {
            (StdIO_Input_FunctType)RBT1_ReadCharBT,
            (StdIO_Output_FunctType)RBT1_SendCharBT,
    };
    
    buf[0] = 0;
    for(;;) {
        // Se parseaza comanda (daca exista una...)
        (void)RBT1_ParseLine(buf, sizeof(buf), &BTstdio);
        // Un mic delay pentru a permite un context switch
        OS_vTaskDelay(5 / portTICK_RATE_MS);
    }
}

/*
** ===================================================================
**     Method      :  RBT1_CreateAppTasks (component Robot)
**
**     Description :
**         This method is internal. It is used by Processor Expert only.
** ===================================================================
*/
static void RBT1_CreateAppTasks()
{
    // Task used for getting, parsing and sending custom commands
    while (OS_xTaskCreate(
        RBT1_ReceiveTask,     // Callback
        (signed portCHAR *)"GPSC",      // Task name
        configMINIMAL_STACK_SIZE + 300, // Stack size
        NULL,                           // Task parameter
        tskIDLE_PRIORITY + 1,           // Task priority
        NULL                            // Task handle
        ) != pdPASS);

    // Task used for sending periodically information about battery level
    while (OS_xTaskCreate(
        RBT1_BatteryTask,     // Callback
        (signed portCHAR *)"SPBL",      // Task name
        configMINIMAL_STACK_SIZE + 400, // Stack size
        NULL,                           // Task parameter
        tskIDLE_PRIORITY + 1,           // Task priority
        NULL                            // Task handle
        ) != pdPASS);
}

/*
** ===================================================================
**     Method      :  RBT1_Init (component Robot)
**
**     Description :
**         This method is internal. It is used by Processor Expert only.
** ===================================================================
*/
void RBT1_Init()
{
	vSemaphoreCreateBinary(xSemaphore);
			
    usDevice.state = ECHO_IDLE;
    usDevice.capture = 0;
    usDevice.triggerTop = TriggerTop_Init(NULL);
    usDevice.triggerLeft = TriggerLeft_Init(NULL);
    usDevice.triggerRight = TriggerRight_Init(NULL);
    usDevice.echoDevice = EchoTop_Init(&usDevice);
}

void EventEchoCapture(LDD_TUserData *UserDataPtr, int channel) 
{
    DeviceType *ptr = (DeviceType*) UserDataPtr;
 
    if (ptr->state == ECHO_TRIGGERED) { 
        EchoTop_ResetCounter(ptr->echoDevice);
        ptr->state = ECHO_MEASURE;   
    }
    else if (ptr->state==ECHO_MEASURE) { 
        (void)EchoTop_GetCaptureValue(ptr->echoDevice, channel, &ptr->capture);
        ptr->state = ECHO_FINISHED;
    }
}

static uint16_t MeasureMicros(uint8_t channel)
{
    uint16_t micros = 0;
    
    if (channel == 1) {
    	TriggerTop_SetVal(usDevice.triggerTop);
    	Wait_Waitus(10);
    	usDevice.state = ECHO_TRIGGERED;
    	TriggerTop_ClrVal(usDevice.triggerTop);
    } else if (channel == 0) {
    	TriggerLeft_SetVal(usDevice.triggerLeft);
    	Wait_Waitus(10);
    	usDevice.state = ECHO_TRIGGERED;
    	TriggerLeft_ClrVal(usDevice.triggerLeft);
    } else {
    	TriggerRight_SetVal(usDevice.triggerRight);
    	Wait_Waitus(10);
    	usDevice.state = ECHO_TRIGGERED;
    	TriggerRight_ClrVal(usDevice.triggerRight);
    }
    
    while(usDevice.state != ECHO_FINISHED) {
        if (usDevice.state == ECHO_OVERFLOW) {
            usDevice.state = ECHO_IDLE;
            return 0;
        }
    }
    
    micros = (usDevice.capture * 1000UL) / (EchoTop_CNT_INP_FREQ_U_0 / 1000);
    return micros;
}

static uint16_t calculateAirspeed(uint8_t temperatureCelsius) 
{
    unsigned int airspeed;

    airspeed = 3314 + (6 * temperatureCelsius);
    airspeed -= (airspeed / 100) * 15;
    return airspeed;
}

static uint16_t MeasureMillimeters(uint8_t channel) 
{
    return (MeasureMicros(channel) * 500UL) / calculateAirspeed(25);
}

/* END RBT1. */

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
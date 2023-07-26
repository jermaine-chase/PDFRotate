package com.jerms.file;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;

public class FileRename {
    public static void rename(String path) {
        System.out.println("STARTING RENAME");
        final File folder = new File(path);
        if (!folder.exists()) {
            try {
                folder.createNewFile();
                System.out.println("CREATED DESTINATION FOLDER: " + path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (File fileEntry : folder.listFiles()) {
            if (fileNameMap.has(fileEntry.getName())) {
                File newFile = new File(path + fileNameMap.get(fileEntry.getName()).getAsString());
                fileEntry.renameTo(newFile);
            }
        }
        System.out.println("RENAME COMPLETED");
    }
    private static String fileNameStr = "{\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000001.pdf\": \"Blue_Advantage_Gold_1800_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000002.pdf\": \"Blue_Advantage_Gold_Standard_2000_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000003.pdf\": \"Blue_Advantage_Silver_Total_3500_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000004.pdf\": \"Blue_Advantage_Silver_Simple_0_Ded_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000005.pdf\": \"Blue_Advantage_Silver_Preferred_3100_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000006.pdf\": \"Blue_Advantage_Silver_Secure_1900_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000008.pdf\": \"Blue_Advantage_Silver_Choice_4000_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000009.pdf\": \"Blue_Advantage_Bronze_9100_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000010.pdf\": \"Blue_Advantage_Silver_Standard_5800_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000011.pdf\": \"Blue_Advantage_Bronze_5500_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000012.pdf\": \"Blue_Advantage_Bronze_7000_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000013.pdf\": \"Blue_Advantage_Bronze_Standard_7500_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.01.0000000014.pdf\": \"Blue_Advantage_Catastrophic_9100_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.02.0000000029.pdf\": \"Blue_Advantage_Bronze_7500_HSA_Eligible_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.02.0000000031.pdf\": \"Blue_Home_Gold_1800_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.02.0000000032.pdf\": \"Blue_Home_Gold_Standard_2000_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.02.0000000033.pdf\": \"Blue_Home_Silver_Total_3500_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.02.0000000034.pdf\": \"Blue_Home_Silver_Simple_0_Ded_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.02.0000000035.pdf\": \"Blue_Home_Silver_Preferred_3100_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.02.0000000036.pdf\": \"Blue_Home_Silver_Secure_1900_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.02.0000000038.pdf\": \"Blue_Home_Silver_Choice_4000_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000039.pdf\": \"Blue_Home_Silver_Standard_5800_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000040.pdf\": \"Blue_Home_Bronze_5500_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000041.pdf\": \"Blue_Home_Bronze_7000_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000042.pdf\": \"Blue_Home_Bronze_Standard_7500_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000043.pdf\": \"Blue_Home_Bronze_9100_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000044.pdf\": \"Blue_Home_Catastrophic_9100_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000045.pdf\": \"Blue_Home_Bronze_7500_with_Novant_Health_HSA_Eligible_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000046.pdf\": \"Blue_Home_Gold_1800_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000047.pdf\": \"Blue_Home_Gold_Standard_2000_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000048.pdf\": \"Blue_Home_Silver_Total_3500_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000049.pdf\": \"Blue_Home_Silver_Simple_0_Ded_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000050.pdf\": \"Blue_Home_Silver_Preferred_3100_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000051.pdf\": \"Blue_Home_Silver_Secure_1900_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000053.pdf\": \"Blue_Home_Silver_Choice_4000_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000054.pdf\": \"Blue_Home_Silver_Standard_5800_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000056.pdf\": \"Blue_Home_Bronze_7000_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.03.0000000057.pdf\": \"Blue_Home_Bronze_Standard_7500_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000058.pdf\": \"Blue_Home_Bronze_9100_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000059.pdf\": \"Blue_Home_Catastrophic_9100_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000060.pdf\": \"Blue_Home_Bronze_7500_with_UNC_Health_Alliance_HSA_Eligible_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000061.pdf\": \"Blue_Local_Gold_1800_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000062.pdf\": \"Blue_Local_Gold_Standard_2000_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000063.pdf\": \"Blue_Local_Silver_Total_3500_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000064.pdf\": \"Blue_Local_Silver_Simple_0_Ded_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000065.pdf\": \"Blue_Local_Silver_Preferred_3100_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000066.pdf\": \"Blue_Local_Silver_Secure_1900_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000068.pdf\": \"Blue_Local_Silver_Choice_4000_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000069.pdf\": \"Blue_Local_Bronze_5500_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000070.pdf\": \"Blue_Local_Silver_Standard_5800_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000071.pdf\": \"Blue_Local_Bronze_7000_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000072.pdf\": \"Blue_Local_Bronze_Standard_7500_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000073.pdf\": \"Blue_Local_Bronze_9100_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000074.pdf\": \"Blue_Local_Catastrophic_9100_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000075.pdf\": \"Blue_Local_Bronze_7500_with_Atrium_Health_HSA_Eligible_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.04.0000000076.pdf\": \"Blue_Local_Gold_1800_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000077.pdf\": \"Blue_Local_Gold_Standard_2000_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000078.pdf\": \"Blue_Local_Silver_Total_3500_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000079.pdf\": \"Blue_Local_Silver_Simple_0_Ded_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000080.pdf\": \"Blue_Local_Silver_Preferred_3100_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000081.pdf\": \"Blue_Local_Silver_Secure_1900_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000083.pdf\": \"Blue_Local_Silver_Choice_4000_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000084.pdf\": \"Blue_Local_Silver_Standard_5800_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000085.pdf\": \"Blue_Local_Bronze_5500_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000086.pdf\": \"Blue_Local_Bronze_7000_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000087.pdf\": \"Blue_Local_Bronze_Standard_7500_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000088.pdf\": \"Blue_Local_Bronze_9100_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000089.pdf\": \"Blue_Local_Catastrophic_9100_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000090.pdf\": \"Blue_Local_Bronze_7500_with_Wake_Forest_Baptist_HSA_Eligible_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000091.pdf\": \"Blue_Value_Gold_1800_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000093.pdf\": \"Blue_Value_Silver_Total_3500_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000094.pdf\": \"Blue_Value_Silver_Simple_0_Ded_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.05.0000000095.pdf\": \"Blue_Value_Silver_Preferred_3100_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.06.0000000096.pdf\": \"Blue_Value_Silver_Secure_1900_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.06.0000000098.pdf\": \"Blue_Value_Silver_Choice_4000_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.06.0000000099.pdf\": \"Blue_Value_Silver_Standard_5800_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.06.0000000100.pdf\": \"Blue_Value_Bronze_5500_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.06.0000000101.pdf\": \"Blue_Value_Bronze_7000_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.06.0000000102.pdf\": \"Blue_Value_Bronze_Standard_7500_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.06.0000000103.pdf\": \"Blue_Value_Bronze_9100_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.06.0000000104.pdf\": \"Blue_Value_Catastrophic_9100_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.06.0000000105.pdf\": \"Blue_Value_Bronze_7500_HSA_Eligible_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.06.0000000107.pdf\": \"Blue_Advantage_Dollar_0_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.06.0000000110.pdf\": \"Blue_Advantage_Gold_Standard_Dollar_0_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.07.0000000115.pdf\": \"Blue_Advantage_Silver_Total_3400_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.07.0000000116.pdf\": \"Blue_Advantage_Silver_Total_675_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.07.0000000117.pdf\": \"Blue_Advantage_Silver_Total_0_Ded_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.07.0000000121.pdf\": \"Blue_Advantage_Silver_Simple_0_Ded_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.07.0000000122.pdf\": \"Blue_Advantage_Silver_Simple_0_Ded_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.07.0000000123.pdf\": \"Blue_Advantage_Silver_Simple_0_Ded_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.07.0000000127.pdf\": \"Blue_Advantage_Silver_Preferred_2500_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.07.0000000128.pdf\": \"Blue_Advantage_Silver_Preferred_300_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.07.0000000129.pdf\": \"Blue_Advantage_Silver_Preferred_0_Ded_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.07.0000000133.pdf\": \"Blue_Advantage_Silver_Secure_1500_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.08.0000000134.pdf\": \"Blue_Advantage_Silver_Secure_75_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.08.0000000135.pdf\": \"Blue_Advantage_Silver_Secure_0_Ded_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.08.0000000139.pdf\": \"Blue_Advantage_Silver_Choice_3700_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.08.0000000140.pdf\": \"Blue_Advantage_Silver_Choice_700_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.08.0000000141.pdf\": \"Blue_Advantage_Silver_Choice_0_Ded_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.08.0000000143.pdf\": \"Blue_Advantage_Bronze_7500_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.08.0000000145.pdf\": \"Blue_Advantage_Silver_Standard_Dollar_0_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.08.0000000147.pdf\": \"Blue_Advantage_Silver_Standard_5700_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.08.0000000148.pdf\": \"Blue_Advantage_Silver_Standard_800_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.08.0000000149.pdf\": \"Blue_Advantage_Silver_Standard_Dollar_0_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.09.0000000157.pdf\": \"Blue_Advantage_Bronze_Standard_Dollar_0_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.12.0000000223.pdf\": \"Blue_Home_Dollar_0_with_Novant_Health_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.12.0000000226.pdf\": \"Blue_Home_Gold_Standard_Dollar_0_with_Novant_Health_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.13.0000000231.pdf\": \"Blue_Home_Silver_Total_3400_with_Novant_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.13.0000000232.pdf\": \"Blue_Home_Silver_Total_675_with_Novant_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.13.0000000233.pdf\": \"Blue_Home_Silver_Total_0_Ded_with_Novant_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.13.0000000237.pdf\": \"Blue_Home_Silver_Simple_0_Ded_with_Novant_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.13.0000000238.pdf\": \"Blue_Home_Silver_Simple_0_Ded_with_Novant_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.13.0000000239.pdf\": \"Blue_Home_Silver_Simple_0_Ded_with_Novant_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.13.0000000243.pdf\": \"Blue_Home_Silver_Preferred_2500_with_Novant_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.13.0000000244.pdf\": \"Blue_Home_Silver_Preferred_300_with_Novant_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.13.0000000245.pdf\": \"Blue_Home_Silver_Preferred_0_Ded_with_Novant_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.14.0000000249.pdf\": \"Blue_Home_Silver_Secure_1500_with_Novant_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.14.0000000250.pdf\": \"Blue_Home_Silver_Secure_75_with_Novant_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.14.0000000251.pdf\": \"Blue_Home_Silver_Secure_0_Ded_with_Novant_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.14.0000000255.pdf\": \"Blue_Home_Silver_Choice_3700_with_Novant_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.14.0000000256.pdf\": \"Blue_Home_Silver_Choice_700_with_Novant_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.14.0000000257.pdf\": \"Blue_Home_Silver_Choice_0_Ded_with_Novant_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.14.0000000259.pdf\": \"Blue_Home_Silver_Standard_Dollar_0_with_Novant_Health_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.14.0000000261.pdf\": \"Blue_Home_Silver_Standard_5700_with_Novant_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.14.0000000262.pdf\": \"Blue_Home_Silver_Standard_800_with_Novant_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.14.0000000263.pdf\": \"Blue_Home_Silver_Standard_Dollar_0_with_Novant_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.15.0000000271.pdf\": \"Blue_Home_Bronze_Standard_Dollar_0_with_Novant_Health_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.15.0000000274.pdf\": \"Blue_Home_Bronze_7500_with_Novant_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.15.0000000284.pdf\": \"Blue_Home_Gold_Standard_Dollar_0_with_UNC_Health_Alliance_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.16.0000000289.pdf\": \"Blue_Home_Silver_Total_3400_with_UNC_Health_Alliance_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.16.0000000290.pdf\": \"Blue_Home_Silver_Total_675_with_UNC_Health_Alliance_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.16.0000000291.pdf\": \"Blue_Home_Silver_Total_0_Ded_with_UNC_Health_Alliance_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.16.0000000295.pdf\": \"Blue_Home_Silver_Simple_0_Ded_with_UNC_Health_Alliance_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.16.0000000296.pdf\": \"Blue_Home_Silver_Simple_0_Ded_with_UNC_Health_Alliance_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.16.0000000297.pdf\": \"Blue_Home_Silver_Simple_0_Ded_with_UNC_Health_Alliance_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.16.0000000301.pdf\": \"Blue_Home_Silver_Preferred_2500_with_UNC_Health_Alliance_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.16.0000000302.pdf\": \"Blue_Home_Silver_Preferred_300_with_UNC_Health_Alliance_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.16.0000000303.pdf\": \"Blue_Home_Silver_Preferred_0_Ded_with_UNC_Health_Alliance_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.17.0000000307.pdf\": \"Blue_Home_Silver_Secure_1500_with_UNC_Health_Alliance_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.17.0000000308.pdf\": \"Blue_Home_Silver_Secure_75_with_UNC_Health_Alliance_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.17.0000000309.pdf\": \"Blue_Home_Silver_Secure_0_Ded_with_UNC_Health_Alliance_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.17.0000000313.pdf\": \"Blue_Home_Silver_Choice_3700_with_UNC_Health_Alliance_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.17.0000000314.pdf\": \"Blue_Home_Silver_Choice_700_with_UNC_Health_Alliance_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.17.0000000315.pdf\": \"Blue_Home_Silver_Choice_0_Ded_with_UNC_Health_Alliance_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.17.0000000317.pdf\": \"Blue_Home_Silver_Standard_Dollar_0_with_UNC_Health_Alliance_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.17.0000000319.pdf\": \"Blue_Home_Silver_Standard_5700_with_UNC_Health_Alliance_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.17.0000000320.pdf\": \"Blue_Home_Silver_Standard_800_with_UNC_Health_Alliance_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.17.0000000321.pdf\": \"Blue_Home_Silver_Standard_Dollar_0_with_UNC_Health_Alliance_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.17.0000000323.pdf\": \"Blue_Home_Dollar_0_with_UNC_Health_Alliance_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.18.0000000324.pdf\": \"Blue_Home_Bronze_5500_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.18.0000000329.pdf\": \"Blue_Home_Bronze_Standard_Dollar_0_with_UNC_Health_Alliance_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.18.0000000332.pdf\": \"Blue_Home_Bronze_7500_with_UNC_Health_Alliance_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.18.0000000339.pdf\": \"Blue_Local_Dollar_0_with_Atrium_Health_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.18.0000000342.pdf\": \"Blue_Local_Gold_Standard_Dollar_0_with_Atrium_Health_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.19.0000000347.pdf\": \"Blue_Local_Silver_Total_3400_with_Atrium_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.19.0000000348.pdf\": \"Blue_Local_Silver_Total_675_with_Atrium_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.19.0000000349.pdf\": \"Blue_Local_Silver_Total_0_Ded_with_Atrium_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.19.0000000353.pdf\": \"Blue_Local_Silver_Simple_0_Ded_with_Atrium_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.19.0000000354.pdf\": \"Blue_Local_Silver_Simple_0_Ded_with_Atrium_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.19.0000000355.pdf\": \"Blue_Local_Silver_Simple_0_Ded_with_Atrium_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.19.0000000359.pdf\": \"Blue_Local_Silver_Preferred_2500_with_Atrium_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.19.0000000360.pdf\": \"Blue_Local_Silver_Preferred_300_with_Atrium_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.19.0000000361.pdf\": \"Blue_Local_Silver_Preferred_0_Ded_with_Atrium_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.20.0000000365.pdf\": \"Blue_Local_Silver_Secure_1500_with_Atrium_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.20.0000000366.pdf\": \"Blue_Local_Silver_Secure_75_with_Atrium_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.20.0000000367.pdf\": \"Blue_Local_Silver_Secure_0_Ded_with_Atrium_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.20.0000000371.pdf\": \"Blue_Local_Silver_Choice_3700_with_Atrium_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.20.0000000372.pdf\": \"Blue_Local_Silver_Choice_700_with_Atrium_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.20.0000000373.pdf\": \"Blue_Local_Silver_Choice_0_Ded_with_Atrium_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.20.0000000378.pdf\": \"Blue_Local_Silver_Standard_Dollar_0_with_Atrium_Health_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.20.0000000380.pdf\": \"Blue_Local_Silver_Standard_5700_with_Atrium_Health_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.21.0000000381.pdf\": \"Blue_Local_Silver_Standard_800_with_Atrium_Health_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.21.0000000382.pdf\": \"Blue_Local_Silver_Standard_Dollar_0_with_Atrium_Health_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.21.0000000387.pdf\": \"Blue_Local_Bronze_Standard_Dollar_0_with_Atrium_Health_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.21.0000000390.pdf\": \"Blue_Local_Bronze_7500_with_Atrium_Health_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.21.0000000397.pdf\": \"Blue_Local_Dollar_0_with_Wake_Forest_Baptist_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.22.0000000400.pdf\": \"Blue_Local_Gold_Standard_Dollar_0_with_Wake_Forest_Baptist_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.22.0000000405.pdf\": \"Blue_Local_Silver_Total_3400_with_Wake_Forest_Baptist_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.22.0000000406.pdf\": \"Blue_Local_Silver_Total_675_with_Wake_Forest_Baptist_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.22.0000000407.pdf\": \"Blue_Local_Silver_Total_0_Ded_with_Wake_Forest_Baptist_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.22.0000000411.pdf\": \"Blue_Local_Silver_Simple_0_Ded_with_Wake_Forest_Baptist_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.22.0000000412.pdf\": \"Blue_Local_Silver_Simple_0_Ded_with_Wake_Forest_Baptist_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.22.0000000413.pdf\": \"Blue_Local_Silver_Simple_0_Ded_with_Wake_Forest_Baptist_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.22.0000000417.pdf\": \"Blue_Local_Silver_Preferred_2500_with_Wake_Forest_Baptist_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.22.0000000418.pdf\": \"Blue_Local_Silver_Preferred_300_with_Wake_Forest_Baptist_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.23.0000000419.pdf\": \"Blue_Local_Silver_Preferred_0_Ded_with_Wake_Forest_Baptist_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.23.0000000423.pdf\": \"Blue_Local_Silver_Secure_1500_with_Wake_Forest_Baptist_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.23.0000000424.pdf\": \"Blue_Local_Silver_Secure_75_with_Wake_Forest_Baptist_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.23.0000000425.pdf\": \"Blue_Local_Silver_Secure_0_Ded_with_Wake_Forest_Baptist_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.23.0000000429.pdf\": \"Blue_Local_Silver_Choice_3700_with_Wake_Forest_Baptist_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.23.0000000430.pdf\": \"Blue_Local_Silver_Choice_700_with_Wake_Forest_Baptist_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.23.0000000431.pdf\": \"Blue_Local_Silver_Choice_0_Ded_with_Wake_Forest_Baptist_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.23.0000000433.pdf\": \"Blue_Local_Silver_Standard_Dollar_0_with_Wake_Forest_Baptist_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.23.0000000435.pdf\": \"Blue_Local_Silver_Standard_5700_with_Wake_Forest_Baptist_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.23.0000000436.pdf\": \"Blue_Local_Silver_Standard_800_with_Wake_Forest_Baptist_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.23.0000000437.pdf\": \"Blue_Local_Silver_Standard_Dollar_0_with_Wake_Forest_Baptist_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.24.0000000445.pdf\": \"Blue_Local_Bronze_Standard_Dollar_0_with_Wake_Forest_Baptist_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.24.0000000448.pdf\": \"Blue_Local_Bronze_7500_with_Wake_Forest_Baptist_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.24.0000000455.pdf\": \"Blue_Value_Dollar_0_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.25.0000000458.pdf\": \"Blue_Value_Gold_Standard_Dollar_0_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.25.0000000459.pdf\": \"Blue_Value_Gold_Standard_2000_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.25.0000000463.pdf\": \"Blue_Value_Silver_Total_3400_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.25.0000000464.pdf\": \"Blue_Value_Silver_Total_675_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.25.0000000465.pdf\": \"Blue_Value_Silver_Total_0_Ded_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.25.0000000469.pdf\": \"Blue_Value_Silver_Simple_0_Ded_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.25.0000000470.pdf\": \"Blue_Value_Silver_Simple_0_Ded_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.25.0000000471.pdf\": \"Blue_Value_Silver_Simple_0_Ded_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.25.0000000475.pdf\": \"Blue_Value_Silver_Preferred_2500_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.26.0000000476.pdf\": \"Blue_Value_Silver_Preferred_300_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.26.0000000477.pdf\": \"Blue_Value_Silver_Preferred_0_Ded_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.26.0000000481.pdf\": \"Blue_Value_Silver_Secure_1500_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.26.0000000482.pdf\": \"Blue_Value_Silver_Secure_75_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.26.0000000483.pdf\": \"Blue_Value_Silver_Secure_0_Ded_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.26.0000000487.pdf\": \"Blue_Value_Silver_Choice_3700_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.26.0000000488.pdf\": \"Blue_Value_Silver_Choice_700_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.26.0000000489.pdf\": \"Blue_Value_Silver_Choice_0_Ded_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.26.0000000491.pdf\": \"Blue_Value_Silver_Standard_Dollar_0_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.26.0000000493.pdf\": \"Blue_Value_Silver_Standard_5700_CSR73_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.26.0000000494.pdf\": \"Blue_Value_Silver_Standard_800_CSR87_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.27.0000000495.pdf\": \"Blue_Value_Silver_Standard_Dollar_0_CSR94_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.27.0000000503.pdf\": \"Blue_Value_Bronze_Standard_Dollar_0_AIAN_2023.pdf\",\n" +
            "\"topaz.bcbsnc.07312022.195740.78820.ucds.imagenow.tst.27.0000000506.pdf\": \"Blue_Value_Bronze_7500_2023.pdf\"}";

    public static JsonObject fileNameMap = (JsonObject) new JsonParser().parse(fileNameStr);
}

package com.devicetracker

object DataHelper {
    fun getDummyUserList(): List<User> {
        val userList: MutableList<User> = mutableListOf()

        userList.add(
            User(
                "1",
                "Alkesh Thakor",
                110111,
                "alkesh.thakor@einfochips.com",
                "",
                ""
            ))
        userList.add(
            User(
                "2",
                "Himanshu Mistri",
                110112,
                "himanshu.mistri@einfochips.com",
                "",
                ""
            ))
        userList.add(
            User(
                "3",
                "Keval Vora",
                110113,
                "keval.vora@einfochips.com",
                "",
                ""
            ))
        userList.add(
            User(
                "4",
                "Chinmay Gajjar(Garligara)",
                110114,
                "Chinmay.gajjar@einfochips.com",
                "",
                ""
            ))
        userList.add(
            User(
                "5",
                "Niraj Gandha",
                110115,
                "niraj@gmail.com",
                "",
                ""
            ))
        userList.add(
            User(
                "6",
                "Vaibhav Kulkarni",
                110116,
                "vaibhav@einfochips.com",
                "",
                ""
            ))

        userList.add(
            User(
                "7",
                "Nagesh Parajapati",
                110117,
                "nagesh@einfochips.com",
                "",
                ""
            ))
        userList.add(
            User(
                "8",
                "Kenil Doshi",
                110118,
                "kenil@einfochips.com",
                "",
                ""
            ))

        return userList
    }


    fun getMemberById(memberId: String): User {
        return getDummyUserList().first { it.id == memberId }
    }

    fun getDeviceDummyList(): List<Device> {
        val deviceList: MutableList<Device> = mutableListOf()
        deviceList.add(Device(1,"Samsung Galaxy S6",DeviceType.TAB.ordinal))
        deviceList.add(Device(2,"Samsung Galaxy Active TAB3",DeviceType.TAB.ordinal))
        deviceList.add(Device(3,"Samsung Galaxy S8",DeviceType.TAB.ordinal))
        deviceList.add(Device(4,"Samsung Galaxy S8 plus",DeviceType.TAB.ordinal))
        deviceList.add(Device(5,"Samsung Galaxy S8 ultra",DeviceType.TAB.ordinal))
        deviceList.add(Device(6,"Samsung Galaxy S9",DeviceType.TAB.ordinal))

        deviceList.add(Device(7,"Trizon USB",DeviceType.USB.ordinal))
        deviceList.add(Device(8,"TORSO one",DeviceType.PROB.ordinal))

        deviceList.add(Device(9,"Lenovo 11",DeviceType.TAB.ordinal))
        deviceList.add(Device(10,"Lenovo 11 Pro",DeviceType.TAB.ordinal))
        deviceList.add(Device(11,"Lenovo 12",DeviceType.TAB.ordinal))
        deviceList.add(Device(12,"Lenovo Yoga 13",DeviceType.TAB.ordinal))
        return deviceList

    }

    fun getDeviceById(deviceId: Int): Device {
        return getDeviceDummyList().first { it.id == deviceId }
    }
}
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

    fun getDeviceDummyList(): List<Device> {
        val deviceList: MutableList<Device> = mutableListOf()
            deviceList.add(Device(1,"Samsung Galaxy S6"))
            deviceList.add(Device(1,"Samsung Galaxy Active TAB3"))
            deviceList.add(Device(1,"Samsung Galaxy S8"))
            deviceList.add(Device(1,"Samsung Galaxy S8 plus"))
            deviceList.add(Device(1,"Samsung Galaxy S8 ultra"))
            deviceList.add(Device(1,"Samsung Galaxy S9"))
            deviceList.add(Device(1,"Lenovo 11"))
            deviceList.add(Device(1,"Lenovo 11 Pro"))
            deviceList.add(Device(1,"Lenovo 12"))
            deviceList.add(Device(1,"Lenovo Yoga 13"))
        return deviceList

    }
}
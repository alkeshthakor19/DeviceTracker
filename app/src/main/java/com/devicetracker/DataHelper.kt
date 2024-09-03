package com.devicetracker

import com.devicetracker.ui.dashbord.assets.Asset
import com.devicetracker.ui.dashbord.assets.AssetType
import com.devicetracker.ui.dashbord.member.Member
import com.google.firebase.Timestamp

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

    fun getDeviceDummyList(): List<Asset> {
        val deviceList: MutableList<Asset> = mutableListOf()
        deviceList.add(Asset(1.toString(),"Samsung Galaxy S6",AssetType.TAB.name))
        deviceList.add(Asset(2.toString(),"Samsung Galaxy Active TAB3", AssetType.TAB.name))
        deviceList.add(Asset(3.toString(),"Samsung Galaxy S8",AssetType.TAB.name))
        deviceList.add(Asset(4.toString(),"Samsung Galaxy S8 plus",AssetType.TAB.name))
        deviceList.add(Asset(5.toString(),"Samsung Galaxy S8 ultra",AssetType.TAB.name))
        deviceList.add(Asset(6.toString(),"Samsung Galaxy S9",AssetType.TAB.name))

        deviceList.add(Asset(7.toString(),"Trizon USB",AssetType.USB.name))
        deviceList.add(Asset(8.toString(),"TORSO one",AssetType.PROBE.name))

        deviceList.add(Asset(9.toString(),"Lenovo 11",AssetType.TAB.name))
        deviceList.add(Asset(10.toString(),"Lenovo 11 Pro",AssetType.TAB.name))
        deviceList.add(Asset(11.toString(),"Lenovo 12",AssetType.TAB.name))
        deviceList.add(Asset(12.toString(),"Lenovo Yoga 13",AssetType.TAB.name))
        return deviceList

    }

    fun getAssignAssetDummyList(): List<Asset> {
        val deviceList: MutableList<Asset> = mutableListOf()
        deviceList.add(Asset(1.toString(),"Samsung Galaxy S6",AssetType.TAB.name))
        deviceList.add(Asset(2.toString(),"Trizon USB",AssetType.USB.name))
        deviceList.add(Asset(3.toString(),"TORSO one",AssetType.PROBE.name))
        deviceList.add(Asset(4.toString(),"LEXSA",AssetType.PROBE.name))

        return deviceList

    }
    fun getAssignMemberDummyList(): List<Member> {
        val memberList: MutableList<Member> = mutableListOf()
        memberList.add(Member("1",150111,"ABC","a@einfochips.com","https://firebasestorage.googleapis.com/v0/b/devicetracker-68d34.appspot.com/o/images%2F82f4455a-d70d-4ad9-9c86-d5702e78822a.jpg?alt=media&token=49c84677-55ff-46ba-8d3a-bc6e8f6d739c",false,
            Timestamp.now()))
        return memberList

    }
}
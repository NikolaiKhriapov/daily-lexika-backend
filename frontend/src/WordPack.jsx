import {Wrap, WrapItem, Spinner, Text} from '@chakra-ui/react'
import SidebarWithHeader from "./shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {errorNotification} from "./services/notification.js";
import WordPackCard from "./components/word-pack/WordPackCard.jsx";
import {getAllWordPacks} from "./services/word-pack.js";

const WordPack = () => {

    const [allWordPacks, setAllWordPacks] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const fetchAllWordPacks = () => {
        setLoading(true);
        getAllWordPacks().then(response => {
            setAllWordPacks(response.data.data.allWordPacks);
        }).catch(error => {
            setError((error.response.data.message))
            errorNotification(
                error.code,
                error.response.data.message
            )
        }).finally(() => {
            setLoading(false);
        })
    }

    useEffect(() => {
        fetchAllWordPacks();
    }, [])

    if (loading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        )
    }

    if (error) {
        return (
            <SidebarWithHeader>
                <Text mt={5}>Ooops, there was an error</Text>
            </SidebarWithHeader>
        )
    }

    if (allWordPacks.length <= 0) {
        return (
            <SidebarWithHeader>
                <Text mt={5}>No wordPacks available</Text>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <Wrap justify={"center"} spacing={"30px"}>
                {allWordPacks.map((wordPack, index) => (
                    <WrapItem key={index}>
                        <WordPackCard
                            {...wordPack}
                            fetchAllWordPacks={fetchAllWordPacks}
                        />
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default WordPack;
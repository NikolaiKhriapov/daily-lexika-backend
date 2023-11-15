import { createStandaloneToast, AlertStatus } from '@chakra-ui/react';

const { toast } = createStandaloneToast();

let currentToast: any = null;

const popupNotification = (title: string, description: string, status: AlertStatus, color: string) => {
  if (currentToast) {
    toast.close(currentToast);
  }

  currentToast = toast({
    title,
    description,
    status,
    colorScheme: color,
    isClosable: true,
    duration: 4 * 1000,
    position: 'top',
  });
};

export const successNotification = (title: string, description: string) => {
  popupNotification(title, description, 'success', 'gray');
};

export const errorNotification = (title: string, description: string) => {
  popupNotification(title, description, 'error', 'red');
};
